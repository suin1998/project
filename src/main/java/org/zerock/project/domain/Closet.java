//package org.zerock.project.domain;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "closet")
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Closet {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String userId;
//
//    @Column(nullable = false)
//    private String imageUrl;
//
//    @Column(nullable = false)
//    private String category;
//
//    private String color;
//    private String brand;
//
//    private Instant createdAt = Instant.now();
//
//    @OneToMany(mappedBy = "clothes", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Tag> tags = new ArrayList<>();
//}
//
