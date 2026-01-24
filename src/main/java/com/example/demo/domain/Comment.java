package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public Comment(Member writer, Article article, String content) {
        this.writer = writer;
        this.article = article;
        this.content = content;
        this.createDate = this.updateDate = LocalDateTime.now();
    }

    public void updateComment(String content) {
        this.content = content;
        this.updateDate = LocalDateTime.now();
    }
}
