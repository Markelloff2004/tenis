package org.cedacri.pingpong.config;

import com.speedment.jpastreamer.application.JPAStreamer;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JPAStreamerConfig {


    Logger logger = LoggerFactory.getLogger(JPAStreamerConfig.class);

    @Bean
    public JPAStreamer jpaStreamer(EntityManagerFactory entityManagerFactory) {
        return JPAStreamer.of(entityManagerFactory);
    }

    @PostConstruct
    public void init(){
        logger.info("JPA Streamer configuration initialized");
    }
}
