package com.example.springprojectmanager;

public class NaoEncontradoException extends RuntimeException {
    public NaoEncontradoException(String message) {
        super(message);
    }
}
