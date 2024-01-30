package com.example.auth.article.dto;

import com.example.auth.article.entity.Article;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;
//    private ArticleWriterDto writer;
    @Setter
    private String writer;

    public static ArticleDto fromEntity(Article entity) {
        return ArticleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter().getUsername())
                .build();
    }
}
