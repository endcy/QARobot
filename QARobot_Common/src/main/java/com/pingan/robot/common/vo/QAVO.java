package com.pingan.robot.common.vo;

import com.pingan.robot.common.utils.DateUtil;
import com.pingan.robot.data.utils.ConstansDefination;

import java.io.Serializable;

/**
 * @Description 分词功能的入参VO
 * @Author chenxx
 * @Date 2019/1/22 10:15
 **/
public class QAVO extends BaseVO implements Serializable, Cloneable {
    private static final long serialVersionUID = -7888786109761308867L;

    private String keyword;
    private String remark;
    private String createTime;
    private String updateTime;
    private Integer isValid;
    private String client;
    private Float simRate;

    public Float getSimRate() {
        return simRate;
    }

    public void setSimRate(Float simRate) {
        this.simRate = simRate;
    }

    public QAVO() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
