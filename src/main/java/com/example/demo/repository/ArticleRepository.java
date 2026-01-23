package com.example.demo.repository;

import com.example.demo.domain.Article;
import com.example.demo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Writer;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findAllByWriter(Member writer);
}
