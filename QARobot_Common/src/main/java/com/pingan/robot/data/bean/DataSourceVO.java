package com.pingan.robot.data.bean;

import java.io.Serializable;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/1/22 20:35
 **/
public class DataSourceVO implements Serializable {
    private static final long serialVersionUID = -1804453720021397652L;
    private Integer id;
    private String type;
    private String driverClassName;
    private String url;
    private String userName;
    private String psWord;
    private Integer target;
    private String mark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsWord() {
        return psWord;
    }

    public void setPsWord(String psWord) {
        this.psWord = psWord;
    }

    public String getMark() {
        return mark;
    }

    public Integer getTarget() {
        return target;
    }

    public void setTarget(Integer target) {
        this.target = target;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "DataSourceVO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", driverClassName='" + driverClassName + '\'' +
                ", url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", psWord='" + psWord + '\'' +
                ", target=" + target +
                ", mark='" + mark + '\'' +
                '}';
    }
}
