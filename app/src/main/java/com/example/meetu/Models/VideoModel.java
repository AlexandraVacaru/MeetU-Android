package com.example.meetu.Models;

public class VideoModel {
    String videoTitle;
    String videoUrl;

    public VideoModel(String videoTitle, String videoUrl) {
        this.videoTitle = videoTitle;
        this.videoUrl = videoUrl;
    }

    public VideoModel() {
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
