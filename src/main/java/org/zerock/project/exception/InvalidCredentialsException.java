package org.zerock.project.exception;

public class InvalidCredentialsException extends RuntimeException {

    // 메시지를 받을 수 있는 생성자
    public InvalidCredentialsException(String message) {
        super(message);
    }
}