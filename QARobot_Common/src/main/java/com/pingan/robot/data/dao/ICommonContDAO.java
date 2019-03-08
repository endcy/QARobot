package com.pingan.robot.data.dao;

import com.pingan.robot.common.vo.QAVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

@Mapper
public interface ICommonContDAO {

    int insert(QAVO content);

    int update(QAVO content);

    int delete(QAVO content);

    QAVO find(QAVO content);

    List<QAVO> findAll(HashMap params);

    List<Integer> findAllSysId();
}
