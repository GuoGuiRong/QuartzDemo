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
