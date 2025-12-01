package org.zerock.project.repository;

import org.zerock.project.entity.Category;
import org.zerock.project.entity.Closet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.User;

import java.util.List;

public interface ClosetRepository extends JpaRepository<Closet, String> {

    // User 객체 기반 조회 (JPA 정석)
    List<Closet> findByUser(User user);

    // User + Category 조회
    List<Closet> findByUserAndCategory(User user, Category category);

    // 태그 검색
    List<Closet> findByUserAndTagsIn(User user, List<String> tags);

}

