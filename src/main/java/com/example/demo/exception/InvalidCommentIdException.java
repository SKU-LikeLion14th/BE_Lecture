package com.example.demo.exception;

public class InvalidCommentIdException extends RuntimeException{
    public InvalidCommentIdException(String message) {
        super(message);
    }
}
