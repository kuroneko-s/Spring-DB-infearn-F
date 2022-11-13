package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static com.cdhdev.spring_db_inflearn.connections.ConnectionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Connection Param 전달.
 */

@Slf4j
class MemberServiceV3_2Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 repository;
    private MemberServiceV3_2 service;

    @BeforeEach
    void beforeEach() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USER_NAME, PASSWORD);

        repository = new MemberRepositoryV3(dataSource);
        service = new MemberServiceV3_2(repository, new DataSourceTransactionManager(dataSource));
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
        assertEquals(afterMemberA.getMoney(), 10000);
        assertEquals(afterMemberB.getMoney(), 10000);
    }

}