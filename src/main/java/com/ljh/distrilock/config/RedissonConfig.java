package com.ljh.distrilock.config;


import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.api.RedissonClient;
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redissonClient(){

        Config config = new Config();

        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        RedissonClient redisson = Redisson.create(config);

        return redisson;

    }
}
