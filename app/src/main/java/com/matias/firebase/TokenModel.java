package com.matias.firebase;

public class TokenModel {
    private String token;

    public TokenModel() {
        // Constructor vacío requerido para Firebase
    }

    public TokenModel(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
