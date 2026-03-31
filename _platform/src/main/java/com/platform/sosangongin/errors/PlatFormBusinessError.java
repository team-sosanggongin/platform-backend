package com.platform.sosangongin.errors;

public class PlatFormBusinessError extends RuntimeException{
    public PlatFormBusinessError() {}
    public PlatFormBusinessError(String message) { super(message); }
}
