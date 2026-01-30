package com.example.demo.controller;

import com.example.demo.DTO.ArticleDTO;
import com.example.demo.domain.Article;
import com.example.demo.service.ArticleService;
import com.example.demo.security.JwtUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/article")
public class ArticleController {

    private final ArticleService articleService;
    private final JwtUtility jwtUtility;

    @PostMapping("/add")
    public ArticleDTO.ArticleRes createArticle(@RequestHeader("Authorization") String token, @RequestBody ArticleDTO.AddArticleReq request){
        Article article = articleService.addArticle(token, request.getTitle(), request.getContent());
        return new ArticleDTO.ArticleRes(article);
    }

    @PutMapping("/update")
    public ArticleDTO.ArticleRes updateArticle(@RequestHeader("Authorization") String token, @RequestBody ArticleDTO.ArticleReq request){
        Article article = articleService.updateArticle(request.getArticleId(), request.getTitle(), request.getContent(), token);
        return new ArticleDTO.ArticleRes(article);
    }

    @DeleteMapping("/{articleId}")
    public String deleteArticle(@RequestHeader("Authorization") String token, @PathVariable("articleId") Long articleId){
        return articleService.deleteArticle(articleId, token);
    }

    @GetMapping("/{articleId}")
    public ArticleDTO.ArticleRes getArticle(@PathVariable("articleId") Long articleId){
        Article article = articleService.findById(articleId);
        return new ArticleDTO.ArticleRes(article);
    }

    @GetMapping("/all")
    public List<ArticleDTO.ArticleRes> allArticleList(){
        List<ArticleDTO.ArticleRes> responseArticles = new ArrayList<>();
        for (Article article : articleService.findAll()) {
            responseArticles.add(new ArticleDTO.ArticleRes(article));
        }
        return responseArticles;
    }

    @GetMapping("/all/{memberId}")
    public List<ArticleDTO.ArticleRes> writerArticleList(@PathVariable("memberId") String memberId){
        List<ArticleDTO.ArticleRes> responseArticles = new ArrayList<>();
        for (Article article : articleService.findAllByWriter(memberId)) {
            responseArticles.add(new ArticleDTO.ArticleRes(article));
        }
        return responseArticles;
    }
}
