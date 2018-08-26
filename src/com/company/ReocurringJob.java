package com.company;

import java.time.ZonedDateTime;


public class ReocurringJob extends Job {

    public long lastTimeInitated;
    public long timeDelta;

    public ReocurringJob(int id, Priorty priorty, int interval) {
        super(id, priorty);
        this.reocurrence = true;
        this.timeDelta = interval * 1000;   //transform to miliseconds
        lastTimeInitated = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    @Override
    public String toString() {
        return "ReocurringJob{" +
                "lastTimeInitated=" + lastTimeInitated +
                ", timeDelta=" + timeDelta +
                ", id=" + id +
                ", priority=" + priority +
                ", reocurrence=" + reocurrence +
                '}';
    }
}
