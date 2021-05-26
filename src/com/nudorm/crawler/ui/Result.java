package com.nudorm.crawler.ui;

import com.nudorm.crawler.domain.Url;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Result {
    SimpleStringProperty parent = new SimpleStringProperty();
    SimpleStringProperty self = new SimpleStringProperty();
    SimpleIntegerProperty status = new SimpleIntegerProperty();

    public Result(String parent, String self, int status) {
        this.parent.set(parent);
        this.self.set(self);
        this.status.set(status);
    }

    public Result(Url url) {
        if(url.getParent() != null) {
            this.parent.set(url.getParent().toString());
        }

        this.self.set(url.getSelf().toString());
        this.status.set(url.getStatus());
    }

    public String getParent() {
        return parent.get();
    }

    public SimpleStringProperty parentProperty() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent.set(parent);
    }

    public String getSelf() {
        return self.get();
    }

    public SimpleStringProperty selfProperty() {
        return self;
    }

    public void setSelf(String self) {
        this.self.set(self);
    }

    public int getStatus() {
        return status.get();
    }

    public SimpleIntegerProperty statusProperty() {
        return status;
    }

    public void setStatus(int status) {
        this.status.set(status);
    }
}
