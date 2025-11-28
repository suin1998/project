package org.zerock.project.repository;

import org.zerock.project.entity.Category;
import org.zerock.project.entity.Closet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosetRepository extends JpaRepository<Closet, Long> {
    // 유저 전체 조회
    List<Closet> findByUserId(Long userId);
    // 유저 + 카테고리 조회
    List<Closet> findByUserIdAndCategory(Long userId, Category category);
    // 태그 검색
    List<Closet> findByUserIdAndTagsIn(Long userId, List<String> tags);
}

