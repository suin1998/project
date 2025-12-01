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
    public ResponseEntity<String> registerBoard(@RequestBody BoardDTO boardDTO) {

        String id = boardService.register(boardDTO);

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO<BoardDTO, Board>> getBoardList(@ModelAttribute PageRequestDTO pageRequestDTO) {

        PageResponseDTO<BoardDTO, Board> boardList = boardService.getList(pageRequestDTO);

        return new ResponseEntity<>(boardList, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable String id) {
        try {
            BoardDTO boardDTO = boardService.get(id);
            return new ResponseEntity<>(boardDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modifyBoard(@PathVariable String id, @RequestBody BoardDTO boardDTO) {
        try {
            boardDTO.setId(id);
            boardService.modify(boardDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeBoard(@PathVariable String id) {
        try {
            boardService.remove(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
