package com.trs.gb;

import com.trs.commons.hybase.DefaultHybaseMapping;
import com.trs.commons.hybase.HybaseTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GbApplication  extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(GbApplication.class);

    }

    public static void main(String[] args) {
        SpringApplication.run(GbApplication.class, args);
    }

    @Bean
    public HybaseTemplate hybaseTemplate() {
        DefaultHybaseMapping defaultHybaseMapping = new DefaultHybaseMapping();
        defaultHybaseMapping.setMappingListFilePath("mappings.list");
        try {
            defaultHybaseMapping.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HybaseTemplate hybaseTemplate = new HybaseTemplate(defaultHybaseMapping);
        return hybaseTemplate;
    }
}

