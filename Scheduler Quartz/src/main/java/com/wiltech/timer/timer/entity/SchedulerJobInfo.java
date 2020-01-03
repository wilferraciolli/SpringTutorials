package com.wiltech.timer.timer.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Scheduler job info. Entity/DTO to reppresent a job and its type. Used to keep track of jobs.
 * This contains if a job is a cron job or not, job class path, cron expression if cron job or repeat time if its a simple job
 */
@Getter
@Setter
@Entity
@Table(catalog = "quartz_demo_db", name = "scheduler_job_info")
public class SchedulerJobInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobName;

    private String jobGroup;

    private String jobClass;

    private String cronExpression;

    private Long repeatTime;

    private Boolean cronJob;
}
