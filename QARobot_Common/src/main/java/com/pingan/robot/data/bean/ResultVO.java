package com.pingan.robot.data.bean;

import com.pingan.robot.data.utils.ConstansDefination;

import java.io.Serializable;

/**
 * 统一返回VO
 */
public class ResultVO<T> implements Serializable {
    private static final long serialVersionUID = 8727955561603027998L;
    private String code;
    private String msg;
    private String status;  //匹配状态 1-存在唯一答案 2-存在问题推荐 3-没有答案
    private T obj;

    public static final ResultVO SUCCESS() {
        return new ResultVO(ConstansDefination.SuccessCode, "Success");
    }

    public static final ResultVO FAILURE() {
        return new ResultVO(ConstansDefination.FailureCode, "Failure");
    }

    public ResultVO() {
    }

    private ResultVO(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(String code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.obj = t;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }

}
