package com.company;

public class Job {

    public enum Priorty {
        LOW,
        NORMAL,
        HIGH
    }

    int id;
    Priorty priority;
    boolean reocurrence;

    public Job(int id,Priorty priority) {
        this.id = id;
        this.priority = priority;
        reocurrence = false;
    }

    public int getId() {
        return id;
    }

    public Priorty getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", priority=" + priority +
                ", reocurrence=" + reocurrence +
                '}';
    }
}
