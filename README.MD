项目介绍:Quartz使用以及SpringMVC数据绑定

  外部项目是基础Quartz的使用
  子项目 SpringQuartz是SpringMvc数据绑定和Quartz整合到Spring的demo

---

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
      
 - Scheduler 调度器
   
    通过Factory模式构建实例,其中StdSchedulerFactory()实现类最为常见
    因为它支持使用声明式配置Quartz.
    而另一个是直接通过java进行配置的DirectSchedulerFactory()较少用。
    
    下面稍微只说一下StdSchedulerFactory()的用法：
    
    ```java_holder_method_tree
        
      //用于绑定jobDetail和trigger，返回值为第一次（首次）执行的时间
      Date d = scheduler.scheduleJob(jobDetail,trigger);
      
      //用于启动调度器
      scheduler.start();
      
      //用于挂起调度器
      scheduler.standby();
      
      //唤醒挂起的调度器
       scheduler.start();
       
      //停止调度器 true表示等待job执行完再停止，false表示先标记为停止状态
       scheduler.shutdown(true);
      
      //判断是否调度器关闭了
      System.out.println(scheduler.isShutdown());
    ```
 - quartz.properties配置文件
    quartz.properties默认时从当前工程目录下读取的，如果没有那么就会读取jar包里面自带的默认
    配置文件进行初始化。
    
    ```properties

    # Default Properties file for use by StdSchedulerFactory
    # to create a Quartz Scheduler Instance, if a different
    # properties file is not explicitly specified.
    #

    org.quartz.scheduler.instanceName: DefaultQuartzScheduler
    org.quartz.scheduler.rmi.export: false
    org.quartz.scheduler.rmi.proxy: false
    org.quartz.scheduler.wrapJobExecutionInUserTransaction: false
    
    org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
    org.quartz.threadPool.threadCount: 10
    org.quartz.threadPool.threadPriority: 5
    org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true
    
    org.quartz.jobStore.misfireThreshold: 60000
    
    org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
    
    ```
      
 下面提供一个实际项目的quartz.properties配置，更多可以参考[官方说明文档](http://www.quartz-scheduler.org/documentation/quartz-2.2.x/configuration/)
 
 ```properties
    
        # Default Properties file for use by StdSchedulerFactory  
        # to create a Quartz Scheduler Instance, if a different  
        # properties file is not explicitly specified.  
        #  
        # ===========================================================================  
        # Configure Main Scheduler Properties 调度器属性  
        # ===========================================================================  
        org.quartz.scheduler.instanceName: DefaultQuartzScheduler  
        #采用自带的id生成方式，让Quartz为我们自动生成id
        #org.quartz.scheduler.instanceid:AUTO  
        org.quartz.scheduler.rmi.export: false  
        org.quartz.scheduler.rmi.proxy: false  
        org.quartz.scheduler.wrapJobExecutionInUserTransaction: false  
        # ===========================================================================    
        # Configure ThreadPool 线程池属性    
        # ===========================================================================  
        #线程池的实现类（一般使用SimpleThreadPool即可满足几乎所有用户的需求）  
        org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool  
        #指定线程数，至少为1（无默认值）(一般设置为1-100直接的整数合适)  
        org.quartz.threadPool.threadCount: 10  
        #设置线程的优先级（最大为java.lang.Thread.MAX_PRIORITY 10，最小为Thread.MIN_PRIORITY 1，默认为5）  
        org.quartz.threadPool.threadPriority: 5  
        #设置SimpleThreadPool的一些属性  
        #设置是否为守护线程  
        #org.quartz.threadpool.makethreadsdaemons = false  
        #org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread: true  
        #org.quartz.threadpool.threadsinheritgroupofinitializingthread=false  
        #线程前缀默认值是：[Scheduler Name]_Worker  
        #org.quartz.threadpool.threadnameprefix=swhJobThead;  
        # 配置全局监听(TriggerListener,JobListener) 则应用程序可以接收和执行 预定的事件通知  
        # ===========================================================================  
        # Configuring a Global TriggerListener 配置全局的Trigger监听器  
        # MyTriggerListenerClass 类必须有一个无参数的构造函数，和 属性的set方法，目前2.2.x只支持原始数据类型的值（包括字符串）  
        # ===========================================================================  
        #org.quartz.triggerListener.NAME.class = com.swh.MyTriggerListenerClass  
        #org.quartz.triggerListener.NAME.propName = propValue  
        #org.quartz.triggerListener.NAME.prop2Name = prop2Value  
        # ===========================================================================  
        # Configuring a Global JobListener 配置全局的Job监听器  
        # MyJobListenerClass 类必须有一个无参数的构造函数，和 属性的set方法，目前2.2.x只支持原始数据类型的值（包括字符串）  
        # ===========================================================================  
        #org.quartz.jobListener.NAME.class = com.swh.MyJobListenerClass  
        #org.quartz.jobListener.NAME.propName = propValue  
        #org.quartz.jobListener.NAME.prop2Name = prop2Value  
        # ===========================================================================    
        # Configure JobStore 存储调度信息（工作，触发器和日历等）  
        # ===========================================================================  
        # 信息保存时间 默认值60秒  
        org.quartz.jobStore.misfireThreshold: 60000  
        #保存job和Trigger的状态信息到内存中的类  
        org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore  
        # ===========================================================================    
        # Configure SchedulerPlugins 插件属性 配置  
        # ===========================================================================  
        # 自定义插件    
        #org.quartz.plugin.NAME.class = com.swh.MyPluginClass  
        #org.quartz.plugin.NAME.propName = propValue  
        #org.quartz.plugin.NAME.prop2Name = prop2Value  
        #配置trigger执行历史日志（可以看到类的文档和参数列表）  
        org.quartz.plugin.triggHistory.class = org.quartz.plugins.history.LoggingTriggerHistoryPlugin    
        org.quartz.plugin.triggHistory.triggerFiredMessage = Trigger {1}.{0} fired job {6}.{5} at: {4, date, HH:mm:ss MM/dd/yyyy}    
        org.quartz.plugin.triggHistory.triggerCompleteMessage = Trigger {1}.{0} completed firing job {6}.{5} at {4, date, HH:mm:ss MM/dd/yyyy} with resulting trigger instruction code: {9}    
        #配置job调度插件  quartz_jobs(jobs and triggers内容)的XML文档    
        #加载 Job 和 Trigger 信息的类   （1.8之前用：org.quartz.plugins.xml.JobInitializationPlugin）  
        org.quartz.plugin.jobInitializer.class = org.quartz.plugins.xml.XMLSchedulingDataProcessorPlugin  
        #指定存放调度器(Job 和 Trigger)信息的xml文件，默认是classpath下quartz_jobs.xml  
        org.quartz.plugin.jobInitializer.fileNames = my_quartz_job2.xml    
        #org.quartz.plugin.jobInitializer.overWriteExistingJobs = false    
        org.quartz.plugin.jobInitializer.failOnFileNotFound = true    
        #自动扫描任务单并发现改动的时间间隔,单位为秒  
        org.quartz.plugin.jobInitializer.scanInterval = 10  
        #覆盖任务调度器中同名的jobDetail,避免只修改了CronExpression所造成的不能重新生效情况  
        org.quartz.plugin.jobInitializer.wrapInUserTransaction = false  
        # ===========================================================================    
        # Sample configuration of ShutdownHookPlugin  ShutdownHookPlugin插件的配置样例  
        # ===========================================================================  
        #org.quartz.plugin.shutdownhook.class = \org.quartz.plugins.management.ShutdownHookPlugin  
        #org.quartz.plugin.shutdownhook.cleanShutdown = true  
        #  
        # Configure RMI Settings 远程服务调用配置  
        #  
        #如果你想quartz-scheduler出口本身通过RMI作为服务器，然后设置“出口”标志true(默认值为false)。  
        #org.quartz.scheduler.rmi.export = false  
        #主机上rmi注册表(默认值localhost)  
        #org.quartz.scheduler.rmi.registryhost = localhost  
        #注册监听端口号（默认值1099）  
        #org.quartz.scheduler.rmi.registryport = 1099  
        #创建rmi注册，false/never：如果你已经有一个在运行或不想进行创建注册  
        # true/as_needed:第一次尝试使用现有的注册，然后再回来进行创建  
        # always:先进行创建一个注册，然后再使用回来使用注册  
        #org.quartz.scheduler.rmi.createregistry = never  
        #Quartz Scheduler服务端端口，默认是随机分配RMI注册表  
        #org.quartz.scheduler.rmi.serverport = 1098  
        #true:链接远程服务调度(客户端),这个也要指定registryhost和registryport，默认为false  
        # 如果export和proxy同时指定为true，则export的设置将被忽略  
        #org.quartz.scheduler.rmi.proxy = false  
 ```
   
* Spring整合Quartz
  
  
  
  * 搭建SpringMVC环境
    pox.xml
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
        <parent>
            <artifactId>QuartzDemo</artifactId>
            <groupId>com.ggr.QuartzDemo</groupId>
            <version>1.0-SNAPSHOT</version>
        </parent>
        <modelVersion>4.0.0</modelVersion>
        <artifactId>SpringQuartz</artifactId>
        <packaging>war</packaging>
        <name>SpringQuartz Maven Webapp</name>
        <url>http://maven.apache.org</url>
        <properties>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            <spring.version>4.3.6.RELEASE</spring.version>
        </properties>
        <dependencies>
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>4.8.2</version>
                <scope>test</scope>
            </dependency>
            <!--引入Quratz-->
            <dependency>
                <groupId>org.quartz-scheduler</groupId>
                <artifactId>quartz</artifactId>
                <version>2.2.3</version>
            </dependency>
            <!-- spring核心包 -->
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-webmvc</artifactId>
                <version>${spring.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context</artifactId>
                <version>${spring.version}</version>
            </dependency>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-aop</artifactId>
                <version>${spring.version}</version>
            </dependency>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-core</artifactId>
                <version>${spring.version}</version>
            </dependency>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context-support</artifactId>
                <version>${spring.version}</version>
            </dependency>
    
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-tx</artifactId>
                <version>${spring.version}</version>
            </dependency>
            <!-- 导入java ee jar 包 -->
            <dependency>
                <groupId>javax</groupId>
                <artifactId>javaee-api</artifactId>
                <version>7.0</version>
            </dependency>
    
        </dependencies>
        <build>
            <finalName>SpringQuratz</finalName>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <configuration>
                        <source>1.8</source>
                        <target>1.8</target>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </project>
    
    ```
    
  * 配置SpringMVC 配置文件
    ```xml
    
    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
    	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:context="http://www.springframework.org/schema/context"
    	   xmlns:mvc="http://www.springframework.org/schema/mvc"
           xmlns:p="http://www.springframework.org/schema/p"
    	   xsi:schemaLocation="http://www.springframework.org/schema/beans
                http://www.springframework.org/schema/beans/spring-beans.xsd  
                http://www.springframework.org/schema/mvc  
                http://www.springframework.org/schema/mvc/spring-mvc.xsd  
                http://www.springframework.org/schema/context  
                http://www.springframework.org/schema/context/spring-context.xsd" default-lazy-init="true">
    
    	<!-- 通过mvc:resources设置静态资源，这样servlet就会处理这些静态资源，而不通过控制器 -->
    	<!-- 设置不过滤内容，比如:css,jquery,img 等资源文件 -->
    	<mvc:resources location="/*.html" mapping="/**.html" />
    	<mvc:resources location="/css/*" mapping="/css/**" />
    	<mvc:resources location="/js/*" mapping="/js/**" />
    	<mvc:resources location="/images/*" mapping="/images/**" />
    	<!-- 设定消息转换的编码为utf-8防止controller返回中文乱码 -->
    	<bean
    		class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter">
    		<property name="messageConverters">
    			<list>
    				<bean
    					class="org.springframework.http.converter.StringHttpMessageConverter">
    					<property name="supportedMediaTypes">
    						<list>
    							<value>text/html;charset=UTF-8</value>
    						</list>
    					</property>
    				</bean>
    			</list>
    		</property>
    	</bean>
    	<!-- 添加注解驱动 -->
    	<mvc:annotation-driven />
    	<!-- 默认扫描的包路径 -->
    	<context:component-scan base-package="com.imooc.springquartz" />
    	<!-- mvc:view-controller可以在不需要Controller处理request的情况，转向到设置的View -->
    	<!-- 像下面这样设置，如果请求为/，则不通过controller，而直接解析为/index.jsp -->
    	<mvc:view-controller path="/" view-name="index" />
    	<bean class="org.springframework.web.servlet.view.UrlBasedViewResolver">
    		<property name="viewClass"
    			value="org.springframework.web.servlet.view.JstlView"></property>
    		<!-- 配置jsp路径前缀 -->
    		<property name="prefix" value="/"></property>
    		<!-- 配置URl后缀 -->
    		<property name="suffix" value=".jsp"></property>
    	</bean>
    
    
    	<bean id="simpleJobDetail" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
    		<property name="targetObject" ref="myBean" />
    		<property name="targetMethod" value="printMessage" />
    	</bean>
    
        <!--定义SimpleJobDetail2-->
        <bean id="SimpleJobDetail2" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
            <property name="targetObject" ref="myBean2" />
            <property name="targetMethod" value="sayHello"/>
        </bean>
        <!--定义触发器-->
        <bean id="myTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean">
            <property name="jobDetail" ref="SimpleJobDetail2"/>
            <property name="startDelay" value="1000"/>
            <property name="repeatInterval" value="2000"/>
        </bean>
    
        <bean id="secondComplexJobDetail" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    
            <property name="jobClass" value="com.imooc.springquartz.quartz.SecondSchduledJob"/>
            <property name="jobDataMap">
                <map>
                    <entry key="myBean2" value-ref="myBean2"/>
                </map>
            </property>
            <property name="durability" value="true"/>
        </bean>
    
    
    	<bean id="firstComplexJobDetail" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    		<property name="jobClass" value="com.imooc.springquartz.quartz.FirstScheduledJob" />
    		<property name="jobDataMap">
    			<map>
    				<entry key="anotherBean" value-ref="anotherBean" />
    			</map>
    		</property>
    		<property name="Durability" value="true"/>				
    	</bean>
    	<!-- 距离当前时间1秒之后执行，之后每隔两秒钟执行一次 -->
    	<bean id="mySimpleTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean">
    	    <property name="jobDetail"  ref="simpleJobDetail"/>
    	    <property name="startDelay"  value="1000"/>
    	    <property name="repeatInterval"  value="2000"/>
    	</bean>
    	
    	<!-- 每隔5秒钟执行一次 -->
    	<bean id="myCronTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
    	    <property name="jobDetail"  ref="firstComplexJobDetail"/>
    	    <property name="cronExpression"  value="0/5 * * ? * *"/>
    	</bean>
    
        <bean id="myTrigger2" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
            <property name="jobDetail" ref="secondComplexJobDetail"/>
            <property name="cronExpression" value="0/30 * * * * ?"/>
        </bean>
    
    
        <!--绑定-->
    	<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    	    <property name="jobDetails">
    	        <list>
    	            <ref bean="simpleJobDetail"/>
    	            <ref bean="firstComplexJobDetail"/>
                    <ref bean="SimpleJobDetail2"/>
                    <ref bean="secondComplexJobDetail"/>
    	        </list>
    	    </property>
    	    <property name="triggers">
    	        <list>
    	            <ref bean="mySimpleTrigger"/>
    	            <ref bean="myCronTrigger"/>
                    <ref bean="myTrigger"/>
                    <ref bean="myTrigger2"/>
    	        </list>
    	    </property>
    	</bean>
    </beans>  
    ```
    
  * 注册SpringMvc 核心
    ```xml

    <?xml version="1.0" encoding="UTF-8"?>
    <web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"
    	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"
    	version="3.0">
    	<display-name>Archetype Created Web Application</display-name>
    	<!-- spring MVC的核心就是DispatcherServlet，使用springMVC的第一步就是将下面的servlet放入web.xml 
    		servlet-name属性非常重要，默认情况下，DispatchServlet会加载这个名字-servlet.xml的文件，如下，就会加载 dispather-servlet.xml，也是在WEN-INF目录下。 -->
    	<servlet>
    		<servlet-name>dispatcher</servlet-name>
    		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    		<load-on-startup>1</load-on-startup>
    	</servlet>
    	<!-- 设置dispatchservlet的匹配模式，通过把dispatchservlet映射到/，默认servlet会处理所有的请求，包括静态资源 -->
    	<servlet-mapping>
    		<servlet-name>dispatcher</servlet-name>
    		<url-pattern>/</url-pattern>
    	</servlet-mapping>
    	<welcome-file-list>
    		<welcome-file>index.jsp</welcome-file>
    	</welcome-file-list>
    
    </web-app>  
    ```
    
  注释；其中Spring提供了Quratz的无缝整合,其中MethodInvokingJobDetailFactoryBean和
  JobDetailFactoryBean 分别提供基于SimpleTrigger和CronTrigger的配置Bean
  
  MethodInvokingJobDetailFactoryBean提供简单的配置方式:
  ```xml
        <!--定义SimpleJobDetail2-->
        <bean id="SimpleJobDetail2" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
            <property name="targetObject" ref="myBean2" />
            <property name="targetMethod" value="sayHello"/>
        </bean>
        <!--定义触发器-->
        <bean id="myTrigger" class="org.springframework.scheduling.quartz.SimpleTriggerFactoryBean">
            <property name="jobDetail" ref="SimpleJobDetail2"/>
            <property name="startDelay" value="1000"/>
            <property name="repeatInterval" value="2000"/>
        </bean>

   ```
  
  JobDetailFactoryBean:支持更加复杂的job
  
   - 创建一个SecondSchduledJob类继承QuartzJobBean
   ```java
        package com.imooc.springquartz.quartz;
        
        import org.quartz.JobExecutionContext;
        import org.quartz.JobExecutionException;
        import org.springframework.scheduling.quartz.QuartzJobBean;
        
        /**
         * Created by GuiRunning on 2017/7/4.
         */
        
        public class SecondSchduledJob extends QuartzJobBean {
        
            //业务逻辑处理类
            private MyBean2 myBean2;
        
            public void setMyBean2(MyBean2 myBean2) {
                this.myBean2 = myBean2;
            }
        
            public MyBean2 getMyBean2() {
                return myBean2;
            }
        
            /**
             *
             * @param jobExecutionContext 业务逻辑处理
             * @throws JobExecutionException
             */
            @Override
            protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                System.out.println("自定义处理逻辑SecondSchduledJob");
                myBean2.sayHello();
            }
        }
   ```
   
   - bean配置
   
   ```xml
    <bean id="secondComplexJobDetail" class="org.springframework.scheduling.quartz.JobDetailFactoryBean">
    
            <property name="jobClass" value="com.imooc.springquartz.quartz.SecondSchduledJob"/>
            <property name="jobDataMap">
                <map>
                    <entry key="myBean2" value-ref="myBean2"/>
                </map>
            </property>
            <property name="durability" value="true"/>
        </bean>
        
        <!--为其配置触发器-->
        <bean id="myTrigger2" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
           <property name="jobDetail" ref="secondComplexJobDetail"/>
           <property name="cronExpression" value="0/30 * * * * ?"/>
        </bean>
   ```
   
 * 不论什么方式配置，最后都要注册到调度器
  ```xml
   <!--绑定-->
  	<bean class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
  	    <property name="jobDetails">
  	        <list>
  	            <ref bean="simpleJobDetail"/>
  	            <ref bean="firstComplexJobDetail"/>
                <ref bean="SimpleJobDetail2"/>
                  <ref bean="secondComplexJobDetail"/>
  	        </list>
  	    </property>
  	    <property name="triggers">
  	        <list>
  	            <ref bean="mySimpleTrigger"/>
  	            <ref bean="myCronTrigger"/>
                  <ref bean="myTrigger"/>
                  <ref bean="myTrigger2"/>
  	        </list>
  	    </property>
  	</bean>
  ```
  注意虽然这里的*jobDetails* 和*triggers* 在初始化阶段已经绑定了，所以顺序可以不一致，但为了
   更好的可读性和后期维护我们建议还是顺序注册(就是两个list中的JobDetail和Trigger一一对应)
        
  

   
           
       
       
        