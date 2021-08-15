package com.example.examregistration;

public class UserHelperClass {
    String name,mail,password, Method,uid;

    private String mfileName;
    private String mImageUrl;
    private String mdocType;
    private String mdocUrl;

    UserHelperClass(){}

    public UserHelperClass(String name,String mail,String password
            ,String Method,String fileName,String imageUrl
            , String docType,String docUrl,String uid) {
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.Method = Method;
        this.uid = uid;

        mfileName = fileName;
        mImageUrl = imageUrl;
        mdocType = docType;
        mdocUrl = docUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String Method) {
        this.Method = Method;
    }

    public String getfileName() {
        return mfileName;
    }

    public void setfileName(String filename) {
        mfileName = filename;
    }


    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getdocType() {
        return mdocType;
    }

    public void setdocType(String docType) {
        mdocType = docType;
    }

    public String getdocUrl() {
        return mdocUrl;
    }

    public void setdocUrl(String docUrl) {
        mdocUrl = docUrl;
    }

}
