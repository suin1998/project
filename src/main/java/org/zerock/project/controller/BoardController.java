package org.zerock.project.controller;


import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.*;
import org.zerock.project.entity.Board;
import org.zerock.project.service.BoardService;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/post")
    public ResponseEntity<String> registerBoard(@RequestBody BoardRegisterDTO boardRegisterDTO) {

        String id = boardService.register(boardRegisterDTO);

        return new ResponseEntity<>(id, HttpStatus.CREATED); // 게시물 등록
    }

    @GetMapping("/post")
    public ResponseEntity<PageResponseDTO<BoardListDTO, Board>> getBoardList(@ModelAttribute PageRequestDTO pageRequestDTO) {

        PageResponseDTO<BoardListDTO, Board> boardList = boardService.getList(pageRequestDTO);

        return new ResponseEntity<>(boardList, HttpStatus.OK); //게시물목록조회

    }

    @GetMapping("/my")
    public ResponseEntity<PageResponseDTO<BoardListDTO, Board>> getMyBoardList(
            @ModelAttribute PageRequestDTO pageRequestDTO,
            @RequestParam String writerId) {

        PageResponseDTO<BoardListDTO, Board> BoardList = boardService.getMyList(pageRequestDTO, writerId);

        return new ResponseEntity<>(BoardList, HttpStatus.OK);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable String id) {
        try {
            BoardDTO boardDTO = boardService.get(id);
            return new ResponseEntity<>(boardDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //특정 게시물 조회
        }
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<Void> removeBoard(@PathVariable String id,
                                            @RequestParam("userId") String removerId) {
        try {
            boardService.remove(id, removerId); // 🚨 게시물 ID와 요청자 ID를 Service로 전달
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found (게시물 없음)
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 🚨 403 Forbidden (권한 없음)
        }
    }
}
