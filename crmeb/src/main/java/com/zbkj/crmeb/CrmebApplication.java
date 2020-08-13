package com.zbkj.crmeb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAsync //开启异步调用
@EnableSwagger2
@Configuration
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class) //去掉数据源
@ComponentScan(basePackages={"com.utils",
        "com.zbkj.crmeb",
        "com.exception",
        "com.common",
        "com.aop"}) //扫描utils包和父包
@MapperScan(basePackages = "com.zbkj.crmeb.*.dao")
public class CrmebApplication{
    public static void main(String[] args) {
        SpringApplication.run(CrmebApplication.class, args);
    }
}
