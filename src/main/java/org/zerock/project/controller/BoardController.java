package org.zerock.project.controller;


import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.*;
import org.zerock.project.entity.Board;
import org.zerock.project.service.BoardService;

import java.util.List;
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<String> registerBoard(@RequestBody BoardRegisterDTO boardRegisterDTO) {

        String id = boardService.register(boardRegisterDTO);

        return new ResponseEntity<>(id, HttpStatus.CREATED); // 게시물 등록
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<BoardListDTO, Board>> getBoardList(@ModelAttribute PageRequestDTO pageRequestDTO) {

        PageResponseDTO<BoardListDTO, Board> boardList = boardService.getList(pageRequestDTO);

        return new ResponseEntity<>(boardList, HttpStatus.OK); //게시물목록조회

    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable String id) {
        try {
            BoardDTO boardDTO = boardService.get(id);
            return new ResponseEntity<>(boardDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //특정 게시물 조회
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyBoard(@PathVariable String id, @RequestBody BoardModifyDTO modifyDTO) {
        try {
            // Service에 ID와 DTO를 함께 전달
            boardService.modify(id, modifyDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // 게시물이 없거나 권한이 없는 경우(Service에서 EntityNotFoundException으로 통일해서 던진다고 가정)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (SecurityException e) {
            // 권한 없음 등의 이유로 거부될 경우 403 Forbidden
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBoard(@PathVariable String id) {
        try {
            boardService.remove(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 게시물 삭제
        }
    }
}
