package com.cdhdev.spring_db_inflearn.service;

import com.cdhdev.spring_db_inflearn.domain.Member;
import com.cdhdev.spring_db_inflearn.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Transaction - 파라미터, 풀 연동
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final MemberRepositoryV2 repository;
    private final DataSource dataSource;

    @SneakyThrows
    public void accountTransfer(String fromId, String toId, int payment) {
        Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false);

            // 비지니스 로직
            bizLogic(connection, fromId, toId, payment);

            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    private void bizLogic(Connection connection, String fromId, String toId, int payment) {
        Member fromMember = repository.findById(fromId);
        Member toMember = repository.findById(toId);

        repository.update(connection, fromId, fromMember.getMoney() - payment);
        validation(toMember);
        repository.update(connection, toId, fromMember.getMoney() + payment);
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
