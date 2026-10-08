package com.example.demodatn.khuyenmai;
public class DiscountValidationException extends IllegalArgumentException {
    private final String field;
    public DiscountValidationException(String field,String message) { super(message);this.field=field; }
    public String getField() { return field; }
}
