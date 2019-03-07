package com.pingan.robot.data.dao.impl;

import com.pingan.robot.data.dao.IDataSourceDAO;
import com.pingan.robot.data.bean.DataSourceVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Repository
@Primary
public class DateSourceDAO implements IDataSourceDAO {
    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int insert(DataSourceVO content) {
        return sqlSessionTemplate.insert("DateSourceMapper.insert", content);
    }

    @Override
    public int update(DataSourceVO content) {
        return sqlSessionTemplate.update("DateSourceMapper.update", content);
    }

    @Override
    public int delete(int id) {
        return sqlSessionTemplate.delete("DateSourceMapper.delete", id);
    }

    @Override
    public DataSourceVO find(int id) {
        return sqlSessionTemplate.selectOne("DateSourceMapper.find", id);
    }

    @Override
    public List<DataSourceVO> findAll(HashMap params){
        return sqlSessionTemplate.selectList("DateSourceMapper.findAll", params);
    }
}
