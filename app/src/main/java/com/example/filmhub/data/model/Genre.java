package com.example.filmhub.data.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    // Getter dan setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
