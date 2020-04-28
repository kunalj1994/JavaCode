package com.java.test.model;

public class WatchPage implements Comparable<WatchPage> {
    private String title;
    private String uri;
    private Integer watchCount;

    public WatchPage(String title, String uri, int watchCount) {
        this.title = title;
        this.uri = uri;
        this.watchCount = watchCount;
    }


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getWatchCount() {
        return this.watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    @Override
    public String toString() {
        return "{" +
            " title='" + getTitle() + "'" +
            ", uri='" + getUri() + "'" +
            ", watchCount='" + getWatchCount() + "'" +
            "}";
    }
    
    @Override
    public int compareTo(WatchPage o) {
        return this.watchCount.compareTo(o.getWatchCount());
    }

}