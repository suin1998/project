package org.zerock.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectApplication.class, args);

        System.out.println("===========================================");
        System.out.println("나만의 옷장 만들기 서버가 시작되었습니다.");
        System.out.println("Server running on: http://localhost:8080/");
        System.out.println("===========================================");
    }

}
