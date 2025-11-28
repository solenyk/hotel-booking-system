package com.kopchak.booking.job;

import lombok.RequiredArgsConstructor;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomAvailabilityJobRunner implements ApplicationRunner {

    private final JobScheduler jobScheduler;
    private final RoomAvailabilityJobService roomAvailabilityJobService;

    @Override
    public void run(ApplicationArguments args) {
        jobScheduler.scheduleRecurrently(
                "inventory-generation",
                Cron.every5minutes(),
//                Cron.daily(2, 0),
                roomAvailabilityJobService::ensureInventoryExists
        );
    }
}
