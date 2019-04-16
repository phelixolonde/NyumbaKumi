package com.nyumba.nyumbakumi;

public class Comment {
    private String commentText;
    private String commentUser;



    public long time;

    public Comment() {
    }

    public Comment(String commentText, String commentUser, long time) {
        this.commentText = commentText;
        this.commentUser = commentUser;
        this.time =time;
    }

    public String getCommentText() {

        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUser() {
        return commentUser;
    }

    public void setCommentUser(String commentUser) {
        this.commentUser = commentUser;
    }

    public long getMessageTime() {
        return time;
    }
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
