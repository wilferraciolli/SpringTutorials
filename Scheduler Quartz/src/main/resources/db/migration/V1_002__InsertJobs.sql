DROP TABLE IF EXISTS `scheduler_job_info`;
CREATE TABLE `scheduler_job_info` (
                                      `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                      `cron_expression` varchar(255) DEFAULT NULL,
                                      `cron_job` bit(1) DEFAULT NULL,
                                      `job_class` varchar(255) DEFAULT NULL,
                                      `job_group` varchar(255) DEFAULT NULL,
                                      `job_name` varchar(255) DEFAULT NULL,
                                      `repeat_time` bigint(20) DEFAULT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=INNODB;

INSERT INTO `scheduler_job_info` (`cron_expression`, `job_class`, `job_group`, `job_name`, `cron_job`, `repeat_time`) VALUES ( '0 0/5 * ? * *', 'com.wiltech.timer.timer.jobs.SampleCronJob', 'Test_Cron', 'Sample Cron', '', NULL);
INSERT INTO `scheduler_job_info` (`cron_expression`, `job_class`, `job_group`, `job_name`, `cron_job`, `repeat_time`) VALUES ( NULL, 'com.wiltech.timer.timer.jobs.SimpleJob', 'Test_Job', 'Simple Job', '\0', '600000');
