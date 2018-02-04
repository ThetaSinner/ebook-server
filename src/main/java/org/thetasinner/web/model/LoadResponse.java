package org.thetasinner.web.model;

public class LoadResponse {
    private String token;

    public LoadResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
