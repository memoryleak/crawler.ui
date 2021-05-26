package com.nudorm.crawler.domain;

import java.net.URI;

public class Url implements Comparable<Url> {
    protected URI parent;
    protected URI self;
    protected UrlType type;
    protected int status = 0;

    public Url(URI parent, URI self, UrlType type, int status) {
        this.parent = parent;
        this.self = self;
        this.type = type;
        this.status = status;
    }

    public Url(URI parent, URI self, UrlType type) {
        this.parent = parent;
        this.self = self;
        this.type = type;
    }

    public URI getParent() {
        return parent;
    }

    public URI getSelf() {
        return self;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UrlType getType() {
        return type;
    }

    public void setType(UrlType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Url o) {
        return self.compareTo(o.getSelf());
    }

    @Override
    public String toString() {
        return self.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Url))
            return false;
        
        Url other = (Url)obj;

        return this.toString().equals(other.toString());
    }
}
