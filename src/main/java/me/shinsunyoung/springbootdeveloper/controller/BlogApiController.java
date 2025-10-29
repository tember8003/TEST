package me.shinsunyoung.springbootdeveloper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.shinsunyoung.springbootdeveloper.domain.Article;
import me.shinsunyoung.springbootdeveloper.dto.AddArticleRequest;
import me.shinsunyoung.springbootdeveloper.dto.ArticleResponse;
import me.shinsunyoung.springbootdeveloper.dto.UpdateArticleRequest;
import me.shinsunyoung.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Article", description = "블로그 글 CRUD API")
@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    @Operation(summary = "글 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "글 생성 실패")
    })
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(
            @Parameter(description = "글 생성 정보", required = true)
            @RequestBody AddArticleRequest request, Principal principal) {
        Article savedArticle = blogService.save(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @Operation(summary = "글 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "글 조회 실패")
    })
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok()
                .body(articles);
    }

    @Operation(summary = "글 조회하기", description = "특정 글 ID로 글 조회하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "글 조회 실패")
    })
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(
            @Parameter(description = "블로그 글 ID", required = true) @PathVariable long id) {
        Article article = blogService.findById(id);
        return ResponseEntity.ok()
                .body(new ArticleResponse(article));
    }

    @Operation(summary = "글 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "글 삭제 실패")
    })
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(
            @Parameter(description = "블로그 글 ID", required = true)
            @PathVariable long id) {
        blogService.delete(id);
        return ResponseEntity.ok()
                .build();
    }

    @Operation(summary = "글 수정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "글 수정 실패")
    })
    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(
            @Parameter(description = "블로그 글 ID", required = true)
            @PathVariable long id,
            @Parameter(description = "글 수정 정보", required = true)
            @RequestBody UpdateArticleRequest request) {
        Article updatedArticle = blogService.update(id, request);
        return ResponseEntity.ok()
                .body(updatedArticle);
    }
}