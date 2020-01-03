package com.wiltech.timer.timer.service;

import com.wiltech.timer.timer.entity.SchedulerJobInfo;

/**
 * The interface Scheduler service.
 */
public interface SchedulerService {

    /**
     * Start all schedulers.
     */
    void startAllSchedulers();

    /**
     * Schedule new job.
     * @param jobInfo the job info
     */
    void scheduleNewJob(SchedulerJobInfo jobInfo);

    /**
     * Update schedule job.
     * @param jobInfo the job info
     */
    void updateScheduleJob(SchedulerJobInfo jobInfo);

    /**
     * Un schedule job boolean.
     * @param jobName the job name
     * @return the boolean
     */
    boolean unScheduleJob(String jobName);

    /**
     * Delete job boolean.
     * @param jobInfo the job info
     * @return the boolean
     */
    boolean deleteJob(SchedulerJobInfo jobInfo);

    /**
     * Pause job boolean.
     * @param jobInfo the job info
     * @return the boolean
     */
    boolean pauseJob(SchedulerJobInfo jobInfo);

    /**
     * Resume job boolean.
     * @param jobInfo the job info
     * @return the boolean
     */
    boolean resumeJob(SchedulerJobInfo jobInfo);

    /**
     * Start job now boolean.
     * @param jobInfo the job info
     * @return the boolean
     */
    boolean startJobNow(SchedulerJobInfo jobInfo);
}
