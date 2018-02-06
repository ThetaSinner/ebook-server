package org.thetasinner.web.model;

public class BookAddRequest {
    private String url;
    private Type type;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        LocalUnmanaged,
        WebLink,
        Other
    }
}
