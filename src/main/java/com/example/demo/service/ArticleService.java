package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.Member;
import com.example.demo.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final MemberService memberService;

    @Transactional
    public Article addArticle(String token, String title, String content) {
        Member member = memberService.tokenToMember(token);
        Article article = new Article(title, content, member);
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Long articleId, String title, String content, String token) {
        Member member = memberService.tokenToMember(token);
        Optional<Article> article = articleRepository.findById(articleId);
        if(article.isEmpty()){
            return null;
        }
        if (article.get().getWriter() == member) {
            article.get().update(title, content);
        }
        return article.get();
    }

    @Transactional
    public String deleteArticle(Long articleId, String token) {
        Member member = memberService.tokenToMember(token);
        Optional<Article> article = articleRepository.findById(articleId);
        if(article.isEmpty())
        {
            return "해당 게시글이 없습니다.";
        }

        if (article.get().getWriter() == member) {
            articleRepository.delete(article.get());
            return "성공적으로 삭제되었습니다.";
        } else{
            return "회원님의 게시글이 아닙니다.";
        }
    }

    public Article findById(Long articleId){
        Optional<Article> article = articleRepository.findById(articleId);
        if(article.isEmpty()){
            return null;
        }
        return articleRepository.findById(articleId).get();
    }

    public List<Article> findAll(){
        return articleRepository.findAll();
    }

    public List<Article> findAllByWriter(String writerId){
        Member member = memberService.findByUserId(writerId);
        if(member == null){
            return List.of();
        }
        return articleRepository.findAllByWriter(member);
    }
}
