package com.example.newscaster;

public class SectionEditorData {
    private String newsId;
    private String title;
    private String date;
    private String time;
    private String location;
    private String category;
    private String article;
    private String status;
    private String imageUrl;

    // Default constructor required for calls to DataSnapshot.getValue(SectionEditorData.class)
    public SectionEditorData() {
    }

    public SectionEditorData(String newsId, String title, String date, String time, String location, String category, String article, String status, String imageUrl) {
        this.newsId = newsId;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.article = article;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
