package com.ta.slk.sistemlayanankegiatan.Model;

public class Comment {
    private String id;
    private String name;
    private String comment;
    private String date;
    private String photo;

    public Comment(String id, String name, String comment, String date, String photo) {
        this.name = name;
        this.comment = comment;
        this.date = date;
        this.photo = photo;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }

    public String getPhoto() {
        return photo;
    }

    public String getId() {return id;}
}
