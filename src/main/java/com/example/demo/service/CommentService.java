package com.example.demo.service;

import com.example.demo.domain.Article;
import com.example.demo.domain.Comment;
import com.example.demo.domain.Member;
import com.example.demo.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final ArticleService articleService;

    @Transactional
    public Comment saveComment(String token, Long articleId, String content){
        Member member = memberService.tokenToMember(token);
        Article article = articleService.findById(articleId);
        Comment comment = new Comment(member, article, content);
        commentRepository.save(comment);
        return comment;
    }

    @Transactional
    public Comment updateComment(Long commentId, String token, String content){
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) return null;

        Member member = memberService.tokenToMember(token);
        if (member == null) return null;

        Comment comment = optionalComment.get();

        if (!comment.getWriter().getId().equals(member.getId())) return null;

        comment.updateComment(content);
        return comment;
    }

    public List<Comment> articleToComment(Long articleId){
        Article article = articleService.findById(articleId);
        if (article == null) return List.of();
        return commentRepository.findByArticle(article);
    }

    @Transactional
    public String deleteComment(Long commentId, String token) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) return "존재하지 않는 댓글입니다.";

        Member member = memberService.tokenToMember(token);
        if (member == null) return "인증되지 않은 사용자입니다.";

        Comment comment = optionalComment.get();
        if (!comment.getWriter().getId().equals(member.getId()))
            return "댓글 삭제 권한이 없습니다.";

        commentRepository.delete(comment);
        return "댓글이 성공적으로 삭제되었습니다.";
    }
}