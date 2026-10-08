package com.example.demodatn.core;

public class PeopleValidationException extends RuntimeException {
    private final String field;
    public PeopleValidationException(String field,String message) { super(message); this.field=field; }
    public String getField() { return field; }
}
