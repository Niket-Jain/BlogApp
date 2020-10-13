package com.example.blogapp;

import java.util.Date;

public class PostBlog {

    public String description,image_URL,user_id;
    public String timestamp;

    public PostBlog(String description, String image_URL, String user_id, String timestamp) {
        this.description = description;
        this.image_URL = image_URL;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public PostBlog(){
        // Empty Constructor.
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_URL() {
        return image_URL;
    }

    public void setImage_URL(String image_URL) {
        this.image_URL = image_URL;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
