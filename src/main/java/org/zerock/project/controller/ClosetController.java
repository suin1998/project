package org.zerock.project.controller;

import org.zerock.project.domain.Closet;
import org.zerock.project.service.ClosetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/closet")
@RequiredArgsConstructor
public class ClosetController {
    private final ClosetService closetservice;

    @PostMapping
    public Closet add(@RequestBody Closet closet) {
        return closetservice.save(closet);
    }

    @GetMapping("/{userId}")
    public List<Closet> getByUser(@PathVariable String userId) {
        return closetservice.getByUserId(userId);
    }

    @GetMapping("/{userId}/{category}")
    public List<Closet> getByCategory(
            @PathVariable String userId,
            @PathVariable String category
    ) {
        return closetservice.getByCategory(userId, category);
    }
}

