package org.ncc.Location;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueManager {
    //TODO finish it
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private volatile long currentWindowStart = System.currentTimeMillis();
    private volatile int tasksExecutedInWindow = 0;
    public static QueueManager queueManager = new QueueManager();

    public QueueManager() {
        new Thread(this::processTask).start();
    }

    private void processTask() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                manageRateLimit();
                taskQueue.take().run();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void manageRateLimit() throws InterruptedException {
        long now = System.currentTimeMillis();
        long elapsed = now - currentWindowStart;

        if(elapsed > 1000) {
            currentWindowStart = now;
            tasksExecutedInWindow = 0;
        }
        if (tasksExecutedInWindow >= 15) {
            if(1000 - elapsed > 0){
                Thread.sleep(1000 - elapsed);
            }
            currentWindowStart = System.currentTimeMillis();
            tasksExecutedInWindow = 0;
        }
        tasksExecutedInWindow++;
    }
    public void submit(Runnable task){
        taskQueue.offer(task);
    }
    public Thread getThread(){
        return Thread.currentThread();
    }
    public static QueueManager getQueueManager() {
        return queueManager;
    }

}
