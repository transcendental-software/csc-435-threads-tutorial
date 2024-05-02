#include <iostream>
#include <vector>
#include <random>
#include <deque>
#include <mutex>
#include <thread>

int task()
{
    int length = 10000000;
    std::vector<int> vector(length);
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_int_distribution<int> uniform_dist(0, length - 1);

    for (auto i = 0; i < length; i++) {
        vector[i] = i;
    }

    for (auto i = 0; i < length; i++) {
        int j = uniform_dist(gen);
        std::swap(vector[i], vector[j]);
    }

    return vector[length - 1];
}

struct queue_t
{
    std::deque<int> deque;
    std::mutex mtx;
};

// Function that will be run by each worker thread
void run_worker(int thread_id, queue_t& queue)
{
    int taskid;
    int rc;

    // Run tasks until the worker thread reads a termination task
    while (true) {
        taskid = 0;

        // Pop an element from the queue, if the queue has elements in it
        {
            std::lock_guard<std::mutex> lock(queue.mtx);
            if (queue.deque.size() >= 1) {
                taskid = queue.deque.front();
                queue.deque.pop_front();
            }
        }

        // If the queue was empty, then try again
        if (taskid == 0) {
            continue;
        }
        
        // If the task is a terminate task, then finish worker thread execution
        if (taskid == -1) {
            break;
        }

        // Run the task
        rc = task();

        std::cout << "Worker " << thread_id << " completed task " << taskid << " (" << rc << ")" << std::endl;;
    }
}

int main(int argc, char** argv)
{
    int num_workers;
    int num_tasks;

    if (argc != 3) {
        std::cerr << "USE: ./run-tasks <number of worker threads> <number of tasks>" << std::endl;
        return 1;
    }

    num_workers = std::atoi(argv[1]);
    num_tasks = std::atoi(argv[2]);

    // Create queue that will house the tasks and the mutex/lock
    queue_t queue;
    std::vector<std::thread> threads;

    // Create the worker threads and assign a unique thread ID for each thread
    for (auto i = 0; i < num_workers; i++) {
        threads.push_back(std::thread(run_worker, i + 1, std::ref(queue)));
    }

    // Add the tasks to the queue
    for (auto i = 0; i < num_tasks; i++) {
        std::lock_guard<std::mutex> lock(queue.mtx);
        queue.deque.push_back(i + 1);
    }

    // Add the termination tasks to the queue, equal in number to the worker threads
    for (auto i = 0; i < num_workers; i++) {
        std::lock_guard<std::mutex> lock(queue.mtx);
        queue.deque.push_back(-1);
    }

    // Wait for all of the worker threads to fininsh executing
    for (auto i = 0; i < num_workers; i++) {
        threads[i].join();
    }

    return 0;
}