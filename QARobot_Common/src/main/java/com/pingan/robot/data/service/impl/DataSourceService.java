package com.pingan.robot.data.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.pingan.robot.data.bean.DataSourceVO;
import com.pingan.robot.data.dao.IDataSourceDAO;
import com.pingan.robot.data.dblink.MutilDBConPool;
import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.utils.DESUtil;
import com.pingan.robot.data.dblink.DataSourceFix;
import com.pingan.robot.data.service.IDataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/1/24 16:06
 **/
@Repository
@Transactional
public class DataSourceService implements IDataSourceService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private IDataSourceDAO dataSourceDAO;

    @Override
    public boolean putDBPool(int dataSourceId) {
        DataSourceVO dataSourceVO = dataSourceDAO.find(dataSourceId);
        return putDB2Pool(dataSourceVO);
    }

    @Override
    public List<Integer> checkAllOriginDB() {
        List<Integer> result = new ArrayList();
        HashMap<String, Object> params = new HashMap();
        params.put("target", 1);
        List<DataSourceVO> list = dataSourceDAO.findAll(params);
        for (DataSourceVO vo : list) {
            if (MutilDBConPool.getDataSource(vo.getId()) != null)
                continue;
            putDB2Pool(vo);
            result.add(vo.getId());
        }
        return result;
    }

    public boolean putDB2Pool(DataSourceVO dataSourceVO) {
        if (dataSourceVO == null)
            return false;
        DruidDataSource datasource;
        try {
            datasource = new DruidDataSource();
            datasource.setDriverClassName(dataSourceVO.getDriverClassName());
            datasource.setUrl(dataSourceVO.getUrl());
            datasource.setUsername(dataSourceVO.getUserName());
            datasource.setPassword(DESUtil.decryptStr(dataSourceVO.getPsWord()));
            //对象连接池中的连接数量配置,可选
            datasource.setInitialSize(5);//初始化的连接数
            datasource.setMaxActive(10); //最大连接数量
        } catch (Exception e) {
            PALogUtil.defaultErrorInfo(logger, e);
            e.printStackTrace();
            return false;
        }
        if (datasource != null) {
            DataSourceFix dataSourceFix = new DataSourceFix(datasource, "select 1");
            MutilDBConPool.putDataSource(dataSourceVO.getId(), dataSourceFix);
            return true;
        }

        return false;
    }
}
