package com.nyumba.nyumbakumi;


public class Report {
    private String messageText;
    private String messageUser;
    private int reportCount;
    private String photo;


    public Report(String messageText, String messageUser,  String photo, int reportCount) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.reportCount = reportCount;
        this.photo = photo;


    }

    public int getReportCount() {
        return reportCount;
    }

    public void setReportCount(int reportCount) {
        this.reportCount = reportCount;
    }

    public Report() {

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }



    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }


}
