package com.company;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TaskManager {

    private int MAXJOBLIMIT = 4;
    private int currentJobAmount = 0;

    private BlockingQueue<Job> lowPriorityQueue;
    private BlockingQueue<Job> normalPriorityQueue;
    private BlockingQueue<Job> highPriorityQueue;

    private final Object lock = new Object();


    //This array will hold all the reoccurring jobs and will insert them to there respective queues
    //when the interval time is reached
    private List<ReocurringJob> reoccurringJobsArray = new ArrayList<ReocurringJob>();

    public TaskManager() {
        lowPriorityQueue = new ArrayBlockingQueue<Job>(MAXJOBLIMIT);
        normalPriorityQueue = new ArrayBlockingQueue<Job>(MAXJOBLIMIT);
        highPriorityQueue = new ArrayBlockingQueue<Job>(MAXJOBLIMIT);

        reoccurringJobsChecker();
    }



    //Public API

    public boolean insertJob(int id, int priorty) throws  InterruptedException{

        Job.Priorty jobPriorty = priortyFromInt(priorty);
        Job job = new Job(id,jobPriorty);
        boolean jobInserted = insertJobToCorrectQueque(job,jobPriorty);
        return  jobInserted;
    }

    public boolean insertReoccurrenceJob(int id, int priorty, int interval) throws  InterruptedException{

        Job.Priorty jobPriorty = priortyFromInt(priorty);
        // considering the input it correct i.e the interval > 0
        ReocurringJob reocurringJob = new ReocurringJob(id,jobPriorty,interval);

        if(!insertJobToCorrectQueque(reocurringJob, reocurringJob.priority)){
            return false;
        }

        if (currentJobAmount < MAXJOBLIMIT){
            reoccurringJobsArray.add(reocurringJob);
        }

        return true;
    }

    public Job getNextJob() throws  InterruptedException{

//        if (lowPriorityQueue.isEmpty() && normalPriorityQueue.isEmpty() && highPriorityQueue.isEmpty()){
//            return -1;
//        }

        while (true){
            synchronized (lock){
                while (currentJobAmount == 0) {
                    lock.wait();
                }

                Job job = fetchJobFromPossibleQueue();

                if( job instanceof ReocurringJob && job.reocurrence == true){
                    for (ReocurringJob reocurringJob :reoccurringJobsArray) {
                        if (reocurringJob.id == job.id){
                            reocurringJob.lastTimeInitated = ZonedDateTime.now().toInstant().toEpochMilli();
                        }
                    }
                }

                lock.notify();
                return job;
            }
        }
    }



    //Private functionality

    private boolean insertJobToCorrectQueque(Job job, Job.Priorty priorty) throws InterruptedException{


        while(true){
            synchronized (lock){
                while (currentJobAmount == MAXJOBLIMIT){
                    lock.wait();
                }

                if (priorty == Job.Priorty.LOW) {
                    lowPriorityQueue.put(job);
                }else if (priorty == Job.Priorty.NORMAL) {
                    normalPriorityQueue.put(job);
                }else if (priorty == Job.Priorty.HIGH) {
                    highPriorityQueue.put(job);
                }

                currentJobAmount++;
                lock.notify();
                return true;
            }
        }
    }

    private Job fetchJobFromPossibleQueue() throws InterruptedException {

        Job job;

        if(!highPriorityQueue.isEmpty()){
            job = highPriorityQueue.take();
        }else if(!normalPriorityQueue.isEmpty()){
            job = normalPriorityQueue.take();
        }else {
            job = lowPriorityQueue.take();
        }

        synchronized (this){
            currentJobAmount--;
            return job;
        }
    }

    //This method will run over reoccurringJobsArray all the time and check if its job's time to execute
    private void reoccurringJobsChecker(){

        new Thread(new Runnable(){

            @Override
            public void run() {
                while(true){
                    for (ReocurringJob reocurringJob :reoccurringJobsArray) {
                        if (checkForExecutionTime(reocurringJob)){
                            try {
                                insertJobToCorrectQueque(reocurringJob, reocurringJob.priority);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }


    //Utility functionlity

    //This method ment to check if the jobs interval has passed
    private boolean checkForExecutionTime(ReocurringJob job){

        long currentTime = ZonedDateTime.now().toInstant().toEpochMilli();
        long passedTime = job.lastTimeInitated + job.timeDelta;

        if ( currentTime >= passedTime) {
            return true;
        }else{
            return false;
        }
    }

    private Job.Priorty priortyFromInt(int priorty){
        Job.Priorty jobPriorty;
        switch (priorty){
            case 0:
                jobPriorty = Job.Priorty.LOW;
                break;
            case 1:
                jobPriorty = Job.Priorty.NORMAL;
                break;
            case 2:
                jobPriorty = Job.Priorty.HIGH;
                break;
            default:
                jobPriorty = Job.Priorty.NORMAL;
                break;
        }
        return jobPriorty;
    }

}
