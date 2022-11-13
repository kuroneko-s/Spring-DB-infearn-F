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
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Transaction - TransactionTemplate
 */

@Slf4j
public class MemberServiceV3_2 {

    private final MemberRepositoryV3 repository;
    private final TransactionTemplate transactionTemplate;

    public MemberServiceV3_2(MemberRepositoryV3 repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void accountTransfer(String fromId, String toId, int payment) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                bizLogic(fromId, toId, payment);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
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
