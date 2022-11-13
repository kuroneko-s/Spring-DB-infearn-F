package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.connections.ConnectionConst;
import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static com.cdhdev.spring_db_inflearn.connections.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 기본 동작, 트랜잭션이 없어서 문제 발생
 */

@Slf4j
class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1 repository;
    private MemberServiceV1 service;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USER_NAME, PASSWORD);

        repository = new MemberRepositoryV1(dataSource);
        service = new MemberServiceV1(repository);
    }

    @AfterEach
    void afterEach() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        repository.save(memberA);
        repository.save(memberB);

        // when
        service.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // then
        Member afterMemberA = repository.findById(memberA.getMemberId());
        Member afterMemberB = repository.findById(memberB.getMemberId());
        assertEquals(afterMemberA.getMoney(), 8000);
        assertEquals(afterMemberB.getMoney(), 12000);
    }

    @Test
    @DisplayName("이체 오류 (unTransaction)")
    void accountTransferEx() {
        // given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEx = new Member(MEMBER_EX, 10000);
        repository.save(memberA);
        repository.save(memberEx);

        // when
        assertThrows(IllegalStateException.class,
                () -> service.accountTransfer(memberA.getMemberId(), memberEx.getMemberId(), 2000));

        // then
        Member afterMemberA = repository.findById(memberA.getMemberId());
        Member afterMemberB = repository.findById(memberEx.getMemberId());
        assertEquals(afterMemberA.getMoney(), 8000);
        assertEquals(afterMemberB.getMoney(), 10000);
    }

}