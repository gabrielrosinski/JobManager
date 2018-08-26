package com.company;

import java.util.Random;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Random random = new Random();

        //Producer - regular job
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        Thread.sleep(700);
                        System.out.println("Able to insert new Job: " + taskManager.insertJob(random.nextInt(100), random.nextInt(3)));
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                }
            }
        });

        //Producer - reoccuring job
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        Thread.sleep(1000);
                        taskManager.insertReoccurrenceJob(random.nextInt(100),random.nextInt(3),random.nextInt(10));
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                }
            }
        });

        //Consummer
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try{
                        Thread.sleep(500);
                        Job newJob = taskManager.getNextJob();
                        System.out.println(newJob.toString());
                    }catch(InterruptedException e){
                        System.out.println(e);
                    }
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

