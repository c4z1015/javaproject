package org.czb.xingcan.admin.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.czb.xingcan.db","org.czb.xingcan.admin.api"})
public class XingcanAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XingcanAdminApiApplication.class, args);
    }

}
