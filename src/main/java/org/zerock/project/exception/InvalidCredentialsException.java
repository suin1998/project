package org.zerock.project.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("유효하지 않은 자격 증명입니다 (이메일 또는 비밀번호 불일치).");
    }

    // 메시지를 받을 수 있는 생성자
    public InvalidCredentialsException(String message) {
        super(message);
    }
}