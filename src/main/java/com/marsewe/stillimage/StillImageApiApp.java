package com.marsewe.stillimage;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@SpringBootApplication
@EnableAsync
public class StillImageApiApp extends AsyncConfigurerSupport {


    public static final String STILL_IMAGE_PATH = "/tmp/stillimages/";



    public static void main(String[] args) {
        SpringApplication.run(StillImageApiApp.class, args);
    }



    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("StillImage-");
        executor.initialize();
        return executor;
    }
}
