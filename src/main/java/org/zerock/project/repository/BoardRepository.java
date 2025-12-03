package org.zerock.project.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.project.entity.Board;
import java.util.List;
import java.util.Optional;


public interface BoardRepository extends JpaRepository<Board, String> {

    Page<Board> findAllByDeletedFalse(Pageable pageable);

    Page<Board> findAllByWriter_IdAndDeletedFalse(String writerId, Pageable pageable);

    Optional<Board> findByIdAndDeletedFalse(String id);

    List<Board> findByWriter_IdAndDeletedFalse(String writerId);

    Page<Board> findAll (Pageable pageable);
}
