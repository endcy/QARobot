package com.pingan.robot.common.log;

import com.pingan.robot.common.utils.DESUtil;
import org.apache.log4j.jdbc.JDBCAppender;

public class JDBCAppenderFix extends JDBCAppender {

    @Override
    public void setPassword(String password) {
        try {
            password = DESUtil.decryptStr(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.databasePassword = password;
    }
}
