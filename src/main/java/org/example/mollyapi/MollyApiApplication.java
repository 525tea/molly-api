package org.example.mollyapi;

import org.apache.poi.util.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class MollyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MollyApiApplication.class, args);
        IOUtils.setByteArrayMaxOverride(1073741824);
    }

}
