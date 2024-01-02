package br.com.senior.burger_place.infra;

public record SimpleResponseError(String message) {
    public SimpleResponseError(Exception exception) {
        this(exception.getMessage());
    }
}