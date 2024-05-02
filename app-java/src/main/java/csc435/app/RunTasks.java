package csc435.app;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SplittableRandom;
import java.util.concurrent.locks.ReentrantLock;

public class RunTasks 
{
    int num_workers;
    int num_tasks;

    public static int task()
    {
        int length = 5000000;
        ArrayList<Integer> list = new ArrayList<Integer>(length);
        SplittableRandom rnd = new SplittableRandom();

        for (int i = 0; i < length; i++) {
            list.add(i);
        }

        for (int i = 0; i < length; i++) {
            int j = rnd.nextInt();
            if (j < 0) {
                j = j * (-1);
            }
            j = j % length;
            Collections.swap(list, i, j);
        }

        return list.get(length - 1);
    }

    class Queue
    {
        public ArrayDeque<Integer> deque;
        public ReentrantLock lock;

        public Queue() {
            deque = new ArrayDeque<Integer>();
            lock = new ReentrantLock();
        }
    }

    class Worker implements Runnable
    {
        int thread_id;
        Queue queue;

        public Worker(int thread_id, Queue queue) {
            this.thread_id = thread_id;
            this.queue = queue;
        }

        // Function that will be run by each worker thread
        @Override
        public void run() {
            int taskid;
            int rc;
            
            // Run tasks until the worker thread reads a termination task
            while (true) {
                taskid = 0;
                
                // Pop an element from the queue, if the queue has elements in it
                queue.lock.lock();
                if (queue.deque.size() >= 1) {
                    taskid = queue.deque.remove();
                }
                queue.lock.unlock();
                
                // If the queue was empty, then try again
                if (taskid == 0) {
                    continue;
                }
                
                // If the task is a terminate task, then finish worker thread execution
                if (taskid == -1) {
                    break;
                }
                
                // Run the task
                rc = RunTasks.task();

                System.out.println("Worker " + thread_id + " completed task " + taskid + " (" + rc + ")");
            }
        }
    }

    public RunTasks(int num_workers, int num_tasks)
    {
        this.num_workers = num_workers;
        this.num_tasks = num_tasks;
    }

    public void run_tasks()
    {
        // Create queue that will house the tasks and the mutex/lock
        Queue queue = new Queue();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        // Create the worker threads and assign a unique thread ID for each thread
        for (int i = 0; i < num_workers; i++) {
            threads.add(new Thread(new Worker(i + 1, queue)));
            threads.get(i).start();
        }

        // Add the tasks to the queue
        for (int i = 0; i < num_tasks; i++) {
            queue.lock.lock();
            queue.deque.add(i + 1);
            queue.lock.unlock();
        }
        
        // Add the termination tasks to the queue, equal in number to the worker threads
        for (int i = 0; i < num_workers; i++) {
            queue.lock.lock();
            queue.deque.add(-1);
            queue.lock.unlock();
        }
        
        // Wait for all of the worker threads to fininsh executing
        for (int i = 0; i < num_workers; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                System.err.println("Could not join thread!");
            }
        }
    }

    public static void main(String[] args) 
    {
        int num_workers;
        int num_tasks;

        if (args.length != 2) {
            System.err.println("USE: java csc435.app.RunTasks <number of worker threads> <number of tasks>");
            System.exit(1);
        }

        num_workers = Integer.parseInt(args[0]);
        num_tasks = Integer.parseInt(args[1]);

        RunTasks runTasks = new RunTasks(num_workers, num_tasks);
        runTasks.run_tasks();
    }
}
