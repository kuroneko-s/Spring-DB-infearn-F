package com.cdhdev.spring_db_inflearn.repository;

import com.cdhdev.spring_db_inflearn.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.Rollback;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    @Rollback
    void crud() throws SQLException {
        Member member = new Member("memberVo1", 100000);
        repository.save(member);

        Member findMember = repository.fineById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertEquals(member, findMember);
    }

    @Test
    void dataSourceDriverManager() {
        new DriverManagerDataSource();
    }

}