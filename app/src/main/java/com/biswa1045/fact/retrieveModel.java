package com.biswa1045.fact;

import com.google.firebase.firestore.Query;

public class retrieveModel {
     String uploader_img;
     String uploader_name;
     String like_img;
     String save_img;

    public retrieveModel() {
    }

    public retrieveModel(String uploader_img, String uploader_name, String like_img, String save_img) {
        this.uploader_img = uploader_img;
        this.uploader_name = uploader_name;
        this.like_img = like_img;
        this.save_img = save_img;
    }

    public String getUploader_img() {
        return uploader_img;
    }

    public void setUploader_img(String uploader_img) {
        this.uploader_img = uploader_img;
    }

    public String getUploader_name() {
        return uploader_name;
    }

    public void setUploader_name(String uploader_name) {
        this.uploader_name = uploader_name;
    }

    public String getLike_img() {
        return like_img;
    }

    public void setLike_img(String like_img) {
        this.like_img = like_img;
    }

    public String getSave_img() {
        return save_img;
    }

    public void setSave_img(String save_img) {
        this.save_img = save_img;
    }

}
