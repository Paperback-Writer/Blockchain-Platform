package com.book.domain;

public class ImageCategory {
    private String name;
    private String folderName;
    
    public ImageCategory(String name, String folderName) {
        this.name = name;
        this.folderName = folderName;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFolderName() {
        return folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}