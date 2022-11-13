package com.cdhdev.spring_db_inflearn;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class UnCheckedExceptionTest {

    @Test
    void unCheckedException() {
        Repository repository = new Repository();
        assertThrows(UnCheckedException.class, () -> repository.unCheckedException());
    }

    static class UnCheckedException extends RuntimeException {
        public UnCheckedException(String message) {
            super(message);
        }
    }

    static class Repository {
        public void unCheckedException() {
            throw new UnCheckedException("Hello");
        }
    }

}
