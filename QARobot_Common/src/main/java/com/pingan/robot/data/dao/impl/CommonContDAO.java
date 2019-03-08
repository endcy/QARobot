package com.pingan.robot.data.dao.impl;

import com.pingan.robot.data.dao.ICommonContDAO;
import com.pingan.robot.common.vo.QAVO;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional
public class CommonContDAO implements ICommonContDAO {
    @Resource
    public SqlSessionTemplate sqlSessionTemplate;

    @Override
    public int insert(QAVO content) {
        return sqlSessionTemplate.insert("commonContentMapper.insert", content);
    }

    @Override
    public int update(QAVO content) {
        return sqlSessionTemplate.update("commonContentMapper.update", content);
    }

    @Override
    public int delete(QAVO content) {
        return sqlSessionTemplate.delete("commonContentMapper.delete", content);
    }

    @Override
    public QAVO find(QAVO content) {
        return sqlSessionTemplate.selectOne("commonContentMapper.find", content);
    }

    @Override
    public List<QAVO> findAll(HashMap params) {
        return sqlSessionTemplate.selectList("commonContentMapper.findAllNoAns", params);
    }

    @Override
    public List<Integer> findAllSysId() {
        return sqlSessionTemplate.selectList("commonContentMapper.findSysId");
    }
}
