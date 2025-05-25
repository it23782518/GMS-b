package com.example.Backend.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("railway")
public class RailwayDatabaseConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        // Force use of Railway MySQL database, ignore environment variables
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .url("jdbc:mysql://root:omxtWcHUmNzxJahkYANETVSgUabNLgiO@crossover.proxy.rlwy.net:31017/railway?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC")
                .username("root")
                .password("omxtWcHUmNzxJahkYANETVSgUabNLgiO")
                .build();
    }
}
