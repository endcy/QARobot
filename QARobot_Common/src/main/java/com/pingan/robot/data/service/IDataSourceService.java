package com.pingan.robot.data.service;

import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/1/24 16:05
 **/
public interface IDataSourceService {
    boolean putDBPool(int dataSourceId);

    List<Integer> checkAllOriginDB();
}
