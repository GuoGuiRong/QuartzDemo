package com.ggr.QuartzDemo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GuiRunning on 2017/7/3.
 */
public class HelloSchduler {
    public static void main(String[] args) throws SchedulerException {
        //创建一个jobDetail实例,该实例与MyJob进行绑定
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).usingJobData("fruit","apple")
                .usingJobData("price","9999.99")
                .withIdentity("myJob").build();

        /**
         * JobDetail的3个重要的属性
         * name gruop class 用于标识某一个具体的JobDetail
         * name 必选参数 表示jobDetail 名称
         * gruop 可选参数 默认为DEFAULT
         */

      /*  System.out.println("jobDetail's name is "+jobDetail.getKey().getName());
        System.out.println("jobDetail's group is "+jobDetail.getKey().getGroup());
        System.out.println("jobDetail's name is "+jobDetail.getKey().getClass());*/

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date1  = new Date();

        date1.setTime(date1.getTime()+2000L);

        Date date2 = new Date();

        date2.setTime(date2.getTime()+10000L);


        String ctime = sf.format(new Date());


        //创建一个trigger实例，定义该job立即执行，并且每间隔2秒钟执行一次，直到永远
      /*  Trigger trigger = TriggerBuilder.newTrigger().withIdentity("myTrigger","group1")
                .usingJobData("PI",3.1415F)
                .startAt(date1)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(2).repeatForever())
                .endAt(date2)
                .build();*/
      SimpleTrigger trigger = TriggerBuilder.newTrigger()
              .startAt(date1)
              .withSchedule(SimpleScheduleBuilder.simpleSchedule()
              .withIntervalInSeconds(2)
              .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
              //.withRepeatCount(3))//重复3次，共执行4次
              .build();

        //创建schduler实例
        SchedulerFactory sfact = new StdSchedulerFactory();

        Scheduler scheduler = sfact.getScheduler();

        scheduler.start();


        System.out.println("当前时间"+ctime);


        //将jobDetail和trigger进行绑定
        scheduler.scheduleJob(jobDetail,trigger);

    }
}
