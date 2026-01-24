package com.example.demo.controller;

import com.example.demo.DTO.CommentDTO;
import com.example.demo.domain.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    @PostMapping("/comment")
    public CommentDTO.CommentResponse createComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO.CommentCreateRequest request){
        jwtUtil.validateJwt(token);
        Comment comment = commentService.saveComment(token, request.getArticleId(), request.getContent());
        return new CommentDTO.CommentResponse(comment);
    }

    @PutMapping("/comment/update")
    public CommentDTO.CommentResponse updateComment(@RequestHeader("Authorization") String token, @RequestBody CommentDTO.CommentUpdateRequest request){
        jwtUtil.validateJwt(token);
        Comment comment = commentService.updateComment(request.getCommentId(), token, request.getContent());
        if(comment == null) return null;
        return new CommentDTO.CommentResponse(comment);
    }

    @GetMapping("/comment/article/{id}")
    public List<CommentDTO.CommentResponse> articleComment(@PathVariable("id") Long articleId){
        List<CommentDTO.CommentResponse> response = new ArrayList<>();
        for(Comment comment : commentService.articleToComment(articleId)){
            response.add(new CommentDTO.CommentResponse(comment));
        }
        return response;
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteComment(@RequestHeader("Authorization") String token, @PathVariable("commentId") Long commentId){
        jwtUtil.validateJwt(token);
        commentService.deleteComment(commentId, token);
    }
}
