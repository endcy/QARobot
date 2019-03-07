package com.pingan.robot.calc.service;

import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/7 10:10
 **/
@Component
@Scope(ConfigurableListableBeanFactory.SCOPE_SINGLETON)
public class InitVecModelService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICommonContDAO commonContDAO;

    public void initDocVecFromDB() {
        List<Integer> sysIds = commonContDAO.findAllSysId();
        for (Integer sysId : sysIds) {
            HashMap map = new HashMap();
            map.put("sysId", sysId);
            List<QAVO> qavoList = commonContDAO.findAll(map);
            try {
                long cost = InitBaseVecModel.initWordAndDocVecModel(sysId, qavoList, null);
                logger.info("初始化系统id：{} 模型数据成功，耗时：{}ms", sysId, cost);
            } catch (Exception e) {
                PALogUtil.defaultErrorInfo(logger, e);
                e.printStackTrace();
                continue;
            }
        }
    }

}
