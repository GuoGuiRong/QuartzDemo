package com.ggr.QuartzDemo;

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
