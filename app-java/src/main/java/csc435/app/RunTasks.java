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

        @Override
        public void run() {
            int taskid;
            int rc;
            
            while (true) {
                taskid = 0;

                queue.lock.lock();
                if (queue.deque.size() >= 1) {
                    taskid = queue.deque.remove();
                }
                queue.lock.unlock();
                
                if (taskid == 0) {
                    continue;
                }

                if (taskid == -1) {
                    break;
                }

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
        Queue queue = new Queue();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (int i = 0; i < num_workers; i++) {
            threads.add(new Thread(new Worker(i + 1, queue)));
            threads.get(i).start();
        }

        for (int i = 0; i < num_tasks; i++) {
            queue.lock.lock();
            queue.deque.add(i + 1);
            queue.lock.unlock();
        }
    
        for (int i = 0; i < num_workers; i++) {
            queue.lock.lock();
            queue.deque.add(-1);
            queue.lock.unlock();
        }
    
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
