package com.ggr.QuartzDemo;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GuiRunning on 2017/7/3.
 */
public class HelloSchdulerForCron {
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


        System.out.println("当前时间"+ctime+"\t"+date1);


        //创建一个trigger实例，定义该job立即执行，并且每间隔2秒钟执行一次，直到永远
        CronTrigger trigger = TriggerBuilder.newTrigger()
                /*.startAt(date1)*/
                .withSchedule(
                        CronScheduleBuilder.
                        //cronSchedule("1,15,30 * * * * ?")//每分中里面的第1,15,30秒执行一次
                        //cronSchedule("0/3 * * * * ?") //每间隔3秒执行一次操作
                        // cronSchedule("* * * ? * 6L")//每个月的最后一个星期五

                        //cronSchedule("* * * L * ?")//每个月最后一天
                        // cronSchedule("* * * ? * LW")//每个月最后一个星期的工作日
                        // cronSchedule("* * 10,14,16 * * ?") //每天上午10点，下午2点，4点
                        //cronSchedule("* 0/30 9-17 * * ?") //朝九晚五工作时间内每半小时
                        //cronSchedule("* * 12 ? * 4") //表示每个星期三中午12点

                        //cronSchedule("* * 12 * * ?") //每天中午12点触发
                        //cronSchedule("* 15 10 * * ?") //每天上午10:15触发
                        //cronSchedule("* 15 10 * * ? 2017") //2017年的每天上午10:15触发
                        //cronSchedule("0 * 14 * * ? ") //在每天下午2点到下午2:59期间的每1分钟触发

                        //cronSchedule("0 0/5 14 * * ?") //在每天下午2点到下午2:55期间的每5分钟触发
                        //cronSchedule("0 0-55/5 14,18 * * ?") //在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
                        //cronSchedule("0 0-5/1 14 * * ?") //在每天下午2点到下午2:05期间的每1分钟触发
                        // cronSchedule("0 10,44 14 ? 3 4") //每年三月的星期三的下午2:10和2:44触发

                        //cronSchedule("0 15 10 ? * 2-6")  //周一至周五的上午10:15触发
                        //cronSchedule("0 15 10 15 * ?")   //每月15日上午10:15触发
                        //cronSchedule("0 15 10 L * ?")   //每月最后一日的上午10:15触发
                        cronSchedule("0 15 10 ? * 6#3")       //每月的第三个星期五上午10:15触发
                )
                .endAt(date2)
                .build();
        //创建schduler实例
        SchedulerFactory sfact = new StdSchedulerFactory();

        Scheduler scheduler = sfact.getScheduler();

        scheduler.start();
        //将jobDetail和trigger进行绑定
        scheduler.scheduleJob(jobDetail,trigger);
    }
}
