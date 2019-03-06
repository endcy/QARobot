package com.pingan.robot.common.vo;

/**
 * @Description 基础VO
 * @Author chenxx
 * @Date 2019/1/21 20:07
 **/
public class BaseVO {
    private Integer id;
    private Integer sysId;
    private String question;
    private String answer;

    public BaseVO() {
    }

    public BaseVO(Integer id, String content) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }


    @Override
    public String toString() {
        return "BaseVO{" +
                "id='" + id + '\'' +
                ", sysId=" + sysId +
                ", question=" + question +
                ", answer=" + answer +
                '}';
    }
}
