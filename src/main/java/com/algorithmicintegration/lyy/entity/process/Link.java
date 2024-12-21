package com.algorithmicintegration.lyy.entity.process;

import java.io.Serializable;

public class Link implements Serializable {
    private String href;
    private String rel;
    private String type;
    private String title;

    public Link(){}

    public Link(String href, String rel, String type, String title){
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
