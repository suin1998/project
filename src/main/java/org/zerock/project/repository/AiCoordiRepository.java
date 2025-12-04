package org.zerock.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.AiCoordi;

import java.util.List;

public interface AiCoordiRepository extends JpaRepository<AiCoordi, String> {
//    List<AiCoordi> findByAi_id(String ai_id);

}

