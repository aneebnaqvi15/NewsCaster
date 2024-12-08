package com.example.newscaster;

public class NewsReport {
    private String headline;
    private String article;
    private String location;
    private String category;
    private String date;
    private String time;
    private String imagePath;
    private String status;

    public NewsReport() {
        // Default constructor required for calls to DataSnapshot.getValue(NewsReport.class)
    }

    public NewsReport(String headline, String article, String location, String category, String date, String time, String imagePath, String status) {
        this.headline = headline;
        this.article = article;
        this.location = location;
        this.category = category;
        this.date = date;
        this.time = time;
        this.imagePath = imagePath;
        this.status = status;
    }

    // Getters and Setters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
