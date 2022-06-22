package org.czb.xingcan.wx.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.czb.xingcan.db"})
public class XingcanWxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XingcanWxApiApplication.class, args);
    }

}
