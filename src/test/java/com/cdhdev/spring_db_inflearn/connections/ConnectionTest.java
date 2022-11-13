package com.cdhdev.spring_db_inflearn.connections;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.cdhdev.spring_db_inflearn.connections.ConnectionConst.*;

@Slf4j
public class ConnectionTest{

    @Test
    void driverManager() throws SQLException {
        Connection connection1 = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        Connection connection2 = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        log.info("connection={}, class={}", connection1, connection1.getClass());
        log.info("connection={}, class={}", connection2, connection2.getClass());
    }
    
    @Test
    void dataSourceDriverManager() {
        // 스프링에서 제공해주는 DriverManager 구현체. 항상 새로운 connection 제공.
        DataSource driverManager = new DriverManagerDataSource(URL, USER_NAME, PASSWORD);
        useDataSource(driverManager);
    }

    @Test
    void dataSourceConnectionPool() throws InterruptedException {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USER_NAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("Hikari Pool");

        useDataSource(dataSource);
        Thread.sleep(1000);
    }

    @SneakyThrows(SQLException.class)
    private void useDataSource(DataSource dataSource) {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();

        log.info("connection={}, class={}", connection1, connection1.getClass());
        log.info("connection={}, class={}", connection2, connection2.getClass());
    }

}
