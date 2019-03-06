package com.pingan.robot.data.dblink;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceFix<T extends DruidDataSource> {
    private T dataSource;
    private String sql;

    public DataSourceFix(T dataSource, String sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    public T getDataSource() {

        return dataSource;
    }

    public void setDataSource(T dataSource) {
        this.dataSource = dataSource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
