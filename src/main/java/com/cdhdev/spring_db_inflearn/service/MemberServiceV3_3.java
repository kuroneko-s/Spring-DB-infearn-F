package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - @Transactional AOP
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 repository;

    @Transactional
    public void accountTransfer(String fromId, String toId, int payment) throws SQLException {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(fromId, fromMember.getMoney() - payment);
        validation(toMember);
        repository.update(toId, fromMember.getMoney() + payment);
    }

    private void validation(Member toMember) {
        if ( toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }


}
