package org.zerock.project.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zerock.project.entity.Board;
import org.zerock.project.entity.User;
import org.zerock.project.entity.User.Gender;
import org.zerock.project.entity.User.UserRole;
import org.zerock.project.repository.BoardRepository;
import org.zerock.project.repository.UserRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 게시글 작성에 사용할 '시스템' 유저의 ID를 상수로 정의합니다.
    private static final String SYSTEM_WRITER_USERNAME = "system_writer";

    @Override
    public void run(String... args) throws Exception {
        log.info("-------------------- 더미 게시글 데이터 주입 시작 --------------------");

        // 1. 게시글 작성을 위한 필수 User 엔티티 확보 (effectively final로 만들기 위해 final 키워드를 사용합니다.)
        final User finalWriter;

        Optional<User> existingUser = userRepository.findByUsername(SYSTEM_WRITER_USERNAME);

        if (existingUser.isPresent()) {
            finalWriter = existingUser.get(); // finalWriter에 할당
            log.info("기존 시스템 작성자 유저(ID: {})를 사용하여 게시글을 생성합니다.", finalWriter.getUsername());
        } else {
            // Foreign Key 제약을 만족하기 위해 유저가 없으면 최소한의 유저를 생성합니다.
            User writer = User.builder()
                    .username(SYSTEM_WRITER_USERNAME)
                    .email("system_writer@zerock.org")
                    .password("{noop}systempass")
                    .nickname("시스템 작성자")
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .gender(Gender.MALE)
                    .role(UserRole.ADMIN) // 관리자 역할로 설정
                    .build();

            finalWriter = userRepository.save(writer); // finalWriter에 할당
            log.warn("시스템 작성자 유저({})가 없어 새로 생성했습니다. (게시글 생성을 위해 필수)", finalWriter.getUsername());
        }

        // 2. 더미 Board 20개 생성
        // 람다 표현식 내부에서 finalWriter 변수를 참조하여 오류를 해결합니다.
        IntStream.rangeClosed(1, 20).forEach(i -> {
            Board board = Board.builder()
                    .title("더미 게시물 제목 " + i)
                    .content("이것은 " + i + "번째 더미 게시물의 내용입니다. 테스트 중입니다.")
                    .userStyle("style_" + (i % 3 + 1))
                    .mainImageUrl(i % 5 == 0 ? "http://placehold.it/600x400" : null)
                    .writer(finalWriter) // **수정됨: finalWriter 참조**
                    .build();

            boardRepository.save(board);
        });

        log.info("더미 게시물 20개 저장 완료. 작성자: {}", finalWriter.getUsername());
        log.info("-------------------- 더미 게시글 데이터 주입 완료 --------------------");
    }
}