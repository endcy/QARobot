package com.pingan.robot.data.dblink;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(ConfigurableListableBeanFactory.SCOPE_SINGLETON)
public class MutilDBConPool {
    @Autowired
    private DataSource dataSource;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private static ConcurrentHashMap<Integer, DataSourceFix> map = new ConcurrentHashMap<>();
    private static int valQueryTimeOut = 5 * 1000;

    /**
     * 根据ID获取DBSource
     *
     * @param id
     * @return
     */
    public static DataSource getDataSource(int id) {
        DataSourceFix dataSourceFix = map.get(id);
        return dataSourceFix != null ? dataSourceFix.getDataSource() : null;
    }

    /**
     * 放入连接池，针对durid连接重新设置后续加入的心跳sql
     *
     * @param id
     * @param dataSourceX
     */
    public static synchronized void putDataSource(int id, DataSourceFix dataSourceX) {
        DruidDataSource dataSource = dataSourceX.getDataSource();
        dataSource.setValidationQuery(dataSourceX.getSql());
        dataSource.setValidationQueryTimeout(valQueryTimeOut);
        map.put(id, dataSourceX);
    }

    /**
     * 轮询数据源，执行心跳连接
     */
    public void heat4Alive() {
        if (map.size() == 0 || dataSource == null) {
            logger.info("DataSource--{} put in pool", dataSource.getClass());
            map.put(0, new DataSourceFix((DruidDataSource) dataSource, "select 1 from dual"));
        }
        logger.info("DruidDataSource number:{}", map.size());
        for (Integer key : map.keySet()) {
            //过滤默认的dbsource
            if (key == 0) continue;
            DataSource dataSource = map.get(key).getDataSource();
            Connection con = null;
            String query = map.get(key).getSql();
            try {
                con = dataSource.getConnection();
            } catch (SQLException e) {
                //todo
                e.printStackTrace();
            }
            if (dataSource instanceof DruidDataSource) {
                try {
                    ((DruidDataSource) dataSource).validateConnection(con);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                PreparedStatement ps = null;
                ResultSet res = null;
                try {
                    ps = con.prepareStatement(query);
                    res = ps.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if (!res.next()) {
                        throw new SQLException("validationQuery didn't return a row");
                    }
                } catch (SQLException e) {
                    PALogUtil.defaultErrorInfo(logger, e);
                    e.printStackTrace();
                }
                JdbcUtils.close(res);
                JdbcUtils.close(ps);
            }
            JdbcUtils.close(con);
            logger.info(DateUtil.getCurrentDateTimeStd() + ": Num--{},type--{} 心跳执行完成:DataSource--{}", key, ((DruidDataSource) dataSource).getDriverClassName(), dataSource);
        }
    }

}
