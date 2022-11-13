package com.cdhdev.spring_db_inflearn.repository;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.cdhdev.spring_db_inflearn.connections.ConnectionConst.*;

@Slf4j
class MemberRepositoryV1Test {
    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach() {
        // DriverManagerDataSource - 항상 새로운 Connection 사용.
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USER_NAME, PASSWORD);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USER_NAME);
        dataSource.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    @SneakyThrows
    void crud(){
        Member member = new Member("memberVo1", 100000);
        repository.save(member);

        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertEquals(member, findMember);

        Thread.sleep(1000);
    }

    @Test
    void dataSourceDriverManager() {
        new DriverManagerDataSource();
    }

}