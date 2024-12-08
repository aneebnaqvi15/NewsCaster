package com.example.newscaster;

public class News {
    private String newsId;
    private String title;
    private String article;
    private String category;
    private String location;
    private String date;
    private String time;
    private String imageUrl;
    private String status;
    private long timestamp;

    // Default no-argument constructor (required for Firebase)
    public News() {
        // This constructor is necessary for Firebase to deserialize the data.
    }

    // Parameterized constructor
    public News(String newsId, String title, String article, String category, String location, String date, String time, String imageUrl, String status, long timestamp) {
        this.newsId = newsId;
        this.title = title;
        this.article = article;
        this.category = category;
        this.location = location;
        this.date = date;
        this.time = time;
        this.imageUrl = imageUrl;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}