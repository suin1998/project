package org.zerock.project.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.Board;
import java.util.List;
import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, String> {

    Page<Board> findAllByDeletedFalse(Pageable pageable);

    List<Board> findByUserIdAndDeletedFalse(String userId);

    Optional<Board> findByIdAndDeletedFalse(String id);

    Page<Board> findAll (Pageable pageable);
}
