package com.book.domain;

public class Image {
    private String name;
    private String path;
    private String title;
    private String description;  
    
    public Image(String name, String path) {
        this.name = name;
        this.path = path;
        this.title = name; 
    }
    
    public Image(String name, String path, String title) {
        this.name = name;
        this.path = path;
        this.title = title;
    }
    
    public Image(String name, String path, String title, String description) {
        this.name = name;
        this.path = path;
        this.title = title;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}