//package org.zerock.project.service;
//
//import org.zerock.project.domain.Closet;
//import org.zerock.project.repository.ClosetRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class ClosetService {
//
//    private final ClosetRepository repo;
//
//    public Closet save(Closet closet) {
//        return repo.save(closet);
//    }
//
//    public List<Closet> getByUserId(String userId) {
//        return repo.findByUserId(userId);
//    }
//
//    public List<Closet> getByCategory(String userId, String category) {
//        return repo.findByUserIdAndCategory(userId, category);
//    }
//
//    public Optional<Closet> getById(Long id) {
//        return repo.findById(id);
//    }
//
//    public void delete(Long id) {
//        repo.deleteById(id);
//    }
//}
//
