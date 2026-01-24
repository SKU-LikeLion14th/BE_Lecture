package com.example.demo.DTO;

import com.example.demo.domain.Comment;
import lombok.Data;

import java.time.LocalDateTime;

public class CommentDTO {
    @Data
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
    }

    @Data
    public static class CommentUpdateRequest {
        private Long commentId;
        private String content;
    }

    @Data
    public static class CommentResponse{
        private String content;
        private LocalDateTime createDate;
        private boolean isUpdate;
        private String writer;
        private String writer_id;

        public CommentResponse(Comment comment){
            this.content = comment.getContent();
            this.createDate = comment.getCreateDate();
            this.isUpdate = (comment.getCreateDate().equals(comment.getUpdateDate())) ? false : true;
            this.writer = comment.getWriter().getUsername();
            this.writer_id = comment.getWriter().getUserId();
        }
    }
}
