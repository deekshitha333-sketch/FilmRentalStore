package com.example.filmRental.Exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}