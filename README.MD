* 创建第一个Quartz实例demo

  1.创建Maven项目
  
  2. 引入相关的maven依赖
  ```xml
  
    <!-- https://mvnrepository.com/artifact/org.quartz-scheduler/quartz -->
      <dependency>
          <groupId>org.quartz-scheduler</groupId>
          <artifactId>quartz</artifactId>
          <version>2.2.1</version>
      </dependency>
    
    ```
  3.编写代码
  
  a.创建一个实现job接口的类,用于描述具体的业务逻辑---MyJob
  ```java
  
  import org.quartz.Job;
  import org.quartz.JobExecutionContext;
  import org.quartz.JobExecutionException;
  
  import java.text.SimpleDateFormat;
  import java.util.Date;
  
  
  /**
   * Created by GuiRunning on 2017/7/3.
   */
  public class MyJob implements Job {
  
      /**
       * 里面包含具体的业务逻辑
       * @param jobExecutionContext 携带参数的上下文
       *
       * @throws JobExecutionException
       */
      public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
  
          SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
          String ctime = sf.format(new Date());
  
          System.out.println("job执行中当前时间"+ctime+",hello quartz");
  
      }
  }

  ```
  
  b.创建测试
  ```java
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
           JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob").build();
   
           /**
            * JobDetail的3个重要的属性
            * name gruop class 用于标识某一个具体的JobDetail
            * name 必选参数 表示jobDetail 名称
            * gruop 可选参数 默认为DEFAULT
            */
   
           System.out.println("jobDetail's name is "+jobDetail.getKey().getName());
           System.out.println("jobDetail's group is "+jobDetail.getKey().getGroup());
           System.out.println("jobDetail's name is "+jobDetail.getKey().getClass());
   
           //创建一个trigger实例，定义该job立即执行，并且每间隔2秒钟执行一次，直到永远
          Trigger trigger = TriggerBuilder.newTrigger()
                                                .withIdentity("myTrigger","group1")
                                                .startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                .withIntervalInSeconds(2).repeatForever()).build();
           //创建schduler实例
           SchedulerFactory sfact = new StdSchedulerFactory();
   
           Scheduler scheduler = sfact.getScheduler();
   
           scheduler.start();
   
           SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
           String ctime = sf.format(new Date());
   
           System.out.println("当前时间"+ctime);
   
          //将jobDetail和trigger进行绑定
           scheduler.scheduleJob(jobDetail,trigger);
   
       }
   }

  ```
  其中：
  
  通过Builder模式创建JodDetail,并赋予其Myjob作为实现逻辑
  ```text
    JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob").build();
  ```
  
  通过Builder模式创建Trigger,并设定其执行时间。
  ```text
      Trigger trigger = TriggerBuilder.newTrigger()
                                      .withIdentity("myTrigger","group1")
                                      .startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                      .withIntervalInSeconds(2).repeatForever()).build();
    ```
  通过Factory创建Schduler,并绑定jobDetail和trigger。
  
  ```text
  SchedulerFactory sfact = new StdSchedulerFactory();
     
  Scheduler scheduler = sfact.getScheduler();
     
  scheduler.start();

  //将jobDetail和trigger进行绑定
  scheduler.scheduleJob(jobDetail,trigger);
  ```
    
    
* jobDetail 和job
    主要为jobDetail标识和起上下文参数JobExecutionContext。
    
  - jobDetail标识:包括name,group,class 构成。
    
    创建的时侯必须指定这些，其中class,name必选,group 可选
    
    ```text
        /*
          JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
          .withIdentity("myJob") group默认DEFAULT
          .build();
        */
         JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity("myJob","group1").build();
    ```
    
    获取jobDetail标识的方式
    ```text
     System.out.println("jobDetail's name is "+jobDetail.getKey().getName());
     System.out.println("jobDetail's group is "+jobDetail.getKey().getGroup());
     System.out.println("jobDetail's name is "+jobDetail.getKey().getClass());
    ```
    
  - JobExecutionContext:表示job运行时的上下文.查看源码
  ```java
  //
  // Source code recreated from a .class file by IntelliJ IDEA
  // (powered by Fernflower decompiler)
  //
  
  package org.quartz;
  
  import java.util.Date;
  import org.quartz.Calendar;
  import org.quartz.Job;
  import org.quartz.JobDataMap;
  import org.quartz.JobDetail;
  import org.quartz.Scheduler;
  import org.quartz.Trigger;
  import org.quartz.TriggerKey;
  
  public interface JobExecutionContext {
      Scheduler getScheduler();
  
      Trigger getTrigger();
  
      Calendar getCalendar();
  
      boolean isRecovering();
  
      TriggerKey getRecoveringTriggerKey() throws IllegalStateException;
  
      int getRefireCount();
  
      JobDataMap getMergedJobDataMap();
  
      JobDetail getJobDetail();
  
      Job getJobInstance();
  
      Date getFireTime();
  
      Date getScheduledFireTime();
  
      Date getPreviousFireTime();
  
      Date getNextFireTime();
  
      String getFireInstanceId();
  
      Object getResult();
  
      void setResult(Object var1);
  
      long getJobRunTime();
  
      void put(Object var1, Object var2);
  
      Object get(Object var1);
  }
  
  ```
  内部携带一个JobDataMap的对象实现了JDK里面的map接口,所以可以通过键值对的方式传递数据。同时还有该Job的所有绑定的Trigger
  和Schduler等等。
  
    - 传递数据的方式
  
      通过JobDetail对象进行设置
      ```text
         JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                        .usingJobData("name","apple")
                        .usingJobData("price","9999.99")
                        .withIdentity("myJob").build();
      ```
      
      通过Trigger对象进行设置
      
      ```text
      Trigger trigger = TriggerBuilder.newTrigger()
                      .withIdentity("myTrigger","group1")
                      .usingJobData("PI",3.1415F)
                      .startNow()
                      .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                      .withIntervalInSeconds(2).repeatForever())
                      .build();
      ```
       
    - 获取上下文中的JobDataMap
        
        1. 通过获取运行时设定该key的对象后，通过设定对象获取
        
        ```text

        String fruit = (String)jobExecutionContext.getJobDetail()
                                                  .getJobDataMap().get("fruit");
        double price = jobExecutionContext.getJobDetail().getJobDataMap()
                                                  .getDoubleValue("price");

        System.out.println("fruit is "+fruit+" and price is "+price);

        Float PI = jobExecutionContext.getTrigger().getJobDataMap()
                                                  .getFloatValue("PI");
        System.out.println("PI is "+ PI);
        ```
        
        2. 直接通过getMergedJobDataMap获取合并后的map,合并规则就是后一次设定的key
        如果和前一次的相同，那么就会覆盖之前的值。
        ```text
         JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
                System.out.println("fruit is "+jobDataMap.get("fruit")+" and price is "+jobDataMap.get("price"));
                System.out.println("PI is "+ jobDataMap.get("PI"));
        ```
        
        3. 通过set/get方法，让api自动注入
        
        ```text
         private String fruit;
            private Double price;
            private Float PI;
        
            public String getFruit() {
                return fruit;
            }
        
            public void setFruit(String fruit) {
                this.fruit = fruit;
            }
        
            public Double getPrice() {
                return price;
            }
        
            public void setPrice(Double price) {
                this.price = price;
            }
        
            public Float getPI() {
                return PI;
            }
        
            public void setPI(Float PI) {
                this.PI = PI;
            }
        ```
         直接通过get方法获取
        ```text
          System.out.println("fruit is "+getFruit()+" and price is "+getPrice());
          System.out.println("PI is "+ getPI());
        ```
        
 * Trigger（触发器）:startAt, endAt 设置工作的首次启动的开始时间和结束时间
    
    - 通过一系列的触发规则，启动Job,如果使用了repeatForever(),那么它会被endAt覆盖(endAt方法的优先级高于repeatForever)。

    - SimpleTrigger:用于在指定时间段执行一次或者多次job
    ```text
     SimpleTrigger trigger = TriggerBuilder.newTrigger()
                          .startAt(date1)
                          .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                          .withIntervalInSeconds(2)
                          .withRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY))
                          //.withRepeatCount(3))//重复3次，共执行4次
                          .build();
    ```
    
    - CronTrigger:基于日历的作业调度器,如果说SimpleTrigger
    是Quartz里面的Timer的封装,那么CronTrigger则是Quartz作为一款优秀的作业调度框架的真正原因。
    
    Cron的概念来源于操作系统,很多操作系统都是采用Cron进行作业调度的。
    
    Cron表达式:
    ```text
    [秒] [分] [时] [日] [月] [周] [年]

    ```
    Cron表达式特殊字符对应表:
    
     字段| 是否必填 |允许值            |运行的特殊字符
    -----|---------|-----------------|------------------
    秒   |  必填   |     0~59          |  , - * / 
    分   |  必填   |     0~59          |  , - * /
    小时 |  必填   |     0~59          |  , - * /
    日   |  必填   |      1-31         |  , - * ? / L W C
    月   |  必填   | 1-12 或者 JAN-DEC  |  , - * /
    周   |  必填   | 1-7 或者 SUN-SAT   |  , - * ? / L C #
    年   |  否     | empty,1970-2099   |  , - * /
      
     ```java_holder_method_tree
     
         "," 表示and
          
         "-" 表示一个区间段，即开始到结束
          
         "*" 表示全选，即用汉语中的“每"或者英文中的every/each/per
          
         "/" 表示一个区间段的时长，例如放在第一位“/10"则表示每10秒
          
         "L" 表示最后,即Last,在日期域表示最后一天（Last day of month） 在星期域表示（last week of month）
          
         "W" 表示weekday，即工作日也就是周一到周五,字符“W”只允许日期域出现。这个字符用于指定日期的最近工作日。例如：如果你在日期域中写 “15W”，表示：这个月15号最近的工作日。
             所以，如果15号是周六，则任务会在14号触发。如果15好是周日，则任务会在周一也就是16号触发。如果是在日期域填写“1W”即使1号是周六，那么任务也只会在下周一，也就是3号触发，
             "W"字符指定的最近工作日是不能够跨月份的。
             字符"W"只能配合一个单独的数值使用，不能够是一个数字段，如：1-15W是错误的。
          
         "C" 表示canlendar,即日历，例如“1C"在星期位上就是包括日历上的星期日
          
         "#" 表示序列，如“#2"表示第二
         
     ```
     
     使用实例:
     
     ```java
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

      ```
      
      
         
           
       
       
        