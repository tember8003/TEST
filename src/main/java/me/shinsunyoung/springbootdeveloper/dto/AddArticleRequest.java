package me.shinsunyoung.springbootdeveloper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Schema(description = "글 생성 Dto")
public class AddArticleRequest {
    @NotBlank(message = "제목은 반드시 입력해야 합니다")
    private String title;

    @NotBlank(message = "내용은 반드시 입력해야 합니다")
    private String content;

    public Article toEntity(String author) {
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}