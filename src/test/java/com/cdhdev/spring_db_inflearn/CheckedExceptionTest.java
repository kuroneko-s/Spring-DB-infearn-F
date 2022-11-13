package com.cdhdev.spring_db_inflearn;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CheckedExceptionTest {

    static class CheckedException extends Exception {
        public CheckedException(String message) {
            super(message);
        }
    }

    @Test
    void checkedException() {
        Repository repository = new Repository();
        try {
            repository.call();
        } catch (CheckedException e) {
            log.info("exceotion={}", e.getMessage(), e);
        }

    }

    static class Repository {
        public void call() throws CheckedException {
            throw new CheckedException("Hello");
        }
    }


}
