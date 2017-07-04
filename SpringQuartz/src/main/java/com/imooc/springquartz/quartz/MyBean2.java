package com.imooc.springquartz.quartz;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GuiRunning on 2017/7/3.
 * 业务逻辑bean
 */
@Component("myBean2")
public class MyBean2 {

    public void sayHello(){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("当前时间"+sf.format(new Date())+"hello wrold!");
    }
}
