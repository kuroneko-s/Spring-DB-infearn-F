package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - TransactionManager
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

    private final MemberRepositoryV3 repository;
    private final PlatformTransactionManager transactionManager;

    @SneakyThrows
    public void accountTransfer(String fromId, String toId, int payment) {
        TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            // 비지니스 로직
            bizLogic(fromId, toId, payment);
            transactionManager.commit(transaction);
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            throw new IllegalStateException(e);
        }
    }

    private void bizLogic(String fromId, String toId, int payment) throws SQLException {
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

    private static void release(Connection connection) {
        if (connection != null) {
            try {
                connection.setAutoCommit(true); // 커넥션 풀 사용시 주의할 점.
                connection.close();
            }catch (Exception e) {
                log.info("error message={}, exception={}", e.getMessage(), e);
            }
        }
    }

}
