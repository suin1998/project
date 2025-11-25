package org.zerock.project.controller;


import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.project.dto.BoardDTO;
import org.zerock.project.dto.PageRequestDTO;
import org.zerock.project.dto.PageResponseDTO;
import org.zerock.project.entity.Board;
import org.zerock.project.service.BoardService;

import java.util.List;
@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Long> registerBoard(@RequestBody BoardDTO boardDTO) {

        Long boardNumber = boardService.register(boardDTO);

        return new ResponseEntity<>(boardNumber, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<BoardDTO, Board>> getBoardList(@ModelAttribute PageRequestDTO pageRequestDTO) {

        PageResponseDTO<BoardDTO, Board> boardList = boardService.getList(pageRequestDTO);

        return new ResponseEntity<>(boardList, HttpStatus.OK);

    }

    @GetMapping("/{boardNumber}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long boardNumber) {
        try {
            BoardDTO boardDTO = boardService.get(boardNumber);
            return new ResponseEntity<>(boardDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{boardNumber}")
    public ResponseEntity<Void> modifyBoard(@PathVariable Long boardNumber, @RequestBody BoardDTO boardDTO) {
        try {
            boardDTO.setBoardNumber(boardNumber);
            boardService.modify(boardDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{boardNumber}")
    public ResponseEntity<Void> removeBoard(@PathVariable Long boardNumber) {
        try {
            boardService.remove(boardNumber);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
