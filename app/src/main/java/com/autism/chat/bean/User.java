package com.autism.chat.bean;

import cn.bmob.v3.BmobUser;


public class User extends BmobUser {

    /**
     * //性别-true-男
     */
    private String avatar;
    private float time;
    private String filePath;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
