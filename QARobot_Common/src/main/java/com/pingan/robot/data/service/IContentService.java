package com.pingan.robot.data.service;

import com.pingan.robot.common.vo.QAVO;

import java.util.HashMap;
import java.util.List;

public interface IContentService {

    int insert(QAVO content);

    int update(QAVO content);

    int delete(QAVO content);

    QAVO find(QAVO content);

    List<QAVO> findAll(HashMap params);

    HashMap<Integer, QAVO> findAllMap(HashMap params);

    List<Integer> findAllSysId();

}
