package com.pc.ecom;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean //Let spring maintain this objects lifecycle
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
