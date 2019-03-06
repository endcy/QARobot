package com.pingan.robot.data.dao;

import com.pingan.robot.data.bean.DataSourceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface IDataSourceDAO {

    int insert(DataSourceVO content);

    int update(DataSourceVO content);

    int delete(int id);

    DataSourceVO find(int id);

    List<DataSourceVO> findAll(HashMap params);

}
