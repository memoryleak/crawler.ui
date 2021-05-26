package com.nudorm.crawler.tasks;

import java.net.URISyntaxException;

public class CoordinatorBuilder {
    protected int nThreadsParsing = 1;
    protected int nThreadsTesting = 1;
    private String[] filterParsing;
    private String[] filterTesting;
    private String startUrl;

    public Coordinator build() throws URISyntaxException {
        return new Coordinator(nThreadsParsing, nThreadsTesting, startUrl, filterParsing, filterTesting);
    }

    public CoordinatorBuilder setParsingThreadsCount(int count) {
        if(count <= 0){
            throw new IllegalArgumentException();
        }

        nThreadsParsing = count;
        return this;
    }

    public CoordinatorBuilder setTestingThreadsCount(int count) {
        if(count <= 0){
            throw new IllegalArgumentException();
        }

        nThreadsTesting = count;
        return this;
    }

    public CoordinatorBuilder setParsingFilter(String[] filter) {
        filterParsing = filter;
        return this;
    }

    public CoordinatorBuilder setTestingFilter(String[] filter) {
        filterTesting = filter;
        return this;
    }

    public CoordinatorBuilder setStartUrl(String url) {
        startUrl = url;
        return this;
    }
}
