package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.cdhdev.spring_db_inflearn.connections.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Transaction - @Transactional AOP
 */

@Slf4j
@SpringBootTest
class MemberServiceV3_3Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    @Autowired private MemberRepositoryV3 repositoryV3;
    @Autowired private MemberServiceV3_3 serviceV3_3;

    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USER_NAME, PASSWORD);
        }

        @Bean
        TransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 repositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 serviceV3_3() {
            return new MemberServiceV3_3(repositoryV3());
        }
    }

    @AfterEach
    void afterEach() {
        repositoryV3.deleteAll();
    }

    @Test
    void proxyCheck() {
        log.info("repository class={}", repositoryV3.getClass());
        log.info("service class={}", serviceV3_3.getClass());
        assertTrue(AopUtils.isAopProxy(serviceV3_3));
        assertFalse(AopUtils.isAopProxy(repositoryV3));
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        repositoryV3.save(memberA);
        repositoryV3.save(memberB);

        // when
        serviceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member afterMemberA = repositoryV3.findById(memberA.getMemberId());
        Member afterMemberB = repositoryV3.findById(memberB.getMemberId());
        assertEquals(afterMemberA.getMoney(), 8000);
        assertEquals(afterMemberB.getMoney(), 12000);
    }

    @Test
    @DisplayName("이체 오류 (unTransaction)")
    void accountTransferEx() {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        repositoryV3.save(memberA);
        repositoryV3.save(memberEx);

        // when
        assertThrows(IllegalStateException.class,
                () -> serviceV3_3.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000));

        // then
        Member afterMemberA = repositoryV3.findById(memberA.getMemberId());
        Member afterMemberB = repositoryV3.findById(memberEx.getMemberId());
        assertEquals(afterMemberA.getMoney(), 10000);
        assertEquals(afterMemberB.getMoney(), 10000);
    }

}