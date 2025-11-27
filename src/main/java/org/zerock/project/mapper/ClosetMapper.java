//package org.zerock.project.mapper;
//
//import org.zerock.project.domain.Closet;
//import org.zerock.project.domain.Tag;
//import org.zerock.project.dto.ClosetRequestDTO;
//
//import java.util.stream.Collectors;
//
//public class ClosetMapper {
//
//    public static Closet toEntity(ClosetRequestDTO dto, String imageUrl) {
//
//        Closet closet = Closet.builder()
//                .userId(dto.getUserId())
//                .imageUrl(imageUrl)
//                .category(dto.getCategory())
//                .color(dto.getColor())
//                .brand(dto.getBrand())
//                .build();
//
//        if (dto.getTags() != null) {
//            closet.setTags(
//                    dto.getTags().stream()
//                            .map(tag -> Tag.builder().tag(tag).closet(closet).build())
//                            .collect(Collectors.toList())
//            );
//        }
//
//        return closet;
//    }
//}
//
