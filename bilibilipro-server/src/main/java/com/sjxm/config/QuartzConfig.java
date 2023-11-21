package com.sjxm.config;

import com.sjxm.constant.TaskQuartzConstant;
import com.sjxm.task.*;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail videoQuartzDetail(){
        return JobBuilder
                .newJob(VideoTask.class)
                .withIdentity(TaskQuartzConstant.VIDEO_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger videoQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.VIDEO_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(videoQuartzDetail())
                .withIdentity(TaskQuartzConstant.VIDEO_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }


    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail accountQuartzDetail(){
        return JobBuilder
                .newJob(AccountTask.class)
                .withIdentity(TaskQuartzConstant.ACCOUNT_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger accountQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.ACCOUNT_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(accountQuartzDetail())
                .withIdentity(TaskQuartzConstant.ACCOUNT_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }


    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail historyQuartzDetail(){
        return JobBuilder
                .newJob(HistoryTask.class)
                .withIdentity(TaskQuartzConstant.HISTORY_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger historyQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.HISTORY_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(historyQuartzDetail())
                .withIdentity(TaskQuartzConstant.HISTORY_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }


    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail commentQuartzDetail(){
        return JobBuilder
                .newJob(CommentTask.class)
                .withIdentity(TaskQuartzConstant.COMMENT_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger commentQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.COMMENT_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(commentQuartzDetail())
                .withIdentity(TaskQuartzConstant.COMMENT_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail likeQuartzDetail(){
        return JobBuilder
                .newJob(LikeTask.class)
                .withIdentity(TaskQuartzConstant.LIKE_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger likeQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.LIKE_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(likeQuartzDetail())
                .withIdentity(TaskQuartzConstant.LIKE_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail searchQuartzDetail(){
        return JobBuilder
                .newJob(SearchTask.class)
                .withIdentity(TaskQuartzConstant.SEARCH_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger searchQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.SEARCH_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(searchQuartzDetail())
                .withIdentity(TaskQuartzConstant.SEARCH_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail messageQuartzDetail(){
        return JobBuilder
                .newJob(MessageTask.class)
                .withIdentity(TaskQuartzConstant.MESSAGE_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger messageQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.MESSAGE_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(messageQuartzDetail())
                .withIdentity(TaskQuartzConstant.MESSAGE_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 指定定时任务工作内容
     * @return
     */
    @Bean
    public JobDetail categoryQuartzDetail(){
        return JobBuilder
                .newJob(CategoryTask.class)
                .withIdentity(TaskQuartzConstant.CATEGORY_TASK_IDENTITY)
                .storeDurably()
                .build();

    }

    /**
     * 触发器，指定运行参数，包括运行次数、运行开始时间和技术时间、运行时长等；
     * 调度器，将Job和Trigger组装起来，使定时任务被真正执行；
     * @return
     */
    @Bean
    public Trigger categoryQuartzTrigger(){
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder
                .simpleSchedule()
                .withIntervalInSeconds(TaskQuartzConstant.CATEGORY_TASK_FREQUENCE)
                .repeatForever();
        return TriggerBuilder.newTrigger().forJob(categoryQuartzDetail())
                .withIdentity(TaskQuartzConstant.CATEGORY_TASK_IDENTITY)
                .withSchedule(scheduleBuilder)
                .build();
    }

}
