package com.pingan.robot.calc.service;

import com.pingan.robot.calc.utils.CalcConstans;
import com.pingan.robot.calc.utils.HanLPConfig;
import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
        List<Integer> sysIds = new ArrayList<>();
        if ("1".equals(HanLPConfig.getConfig("isInitModel4DB")))
            sysIds = commonContDAO.findAllSysId();
        String defVecModelPath = HanLPConfig.getConfig("defVecModelPath");
        logger.info("Init Model Path:{}", defVecModelPath);
        for (Integer sysId : sysIds) {
            HashMap map = new HashMap();
            map.put("sysId", sysId);
            List<QAVO> qavoList = commonContDAO.findAll(map);
            try {
                long cost = InitBaseVecModel.initWordAndDocVecModel(sysId, qavoList, defVecModelPath);
                logger.info("初始化系统id：{} 模型数据成功，耗时：{}ms", sysId, cost);
            } catch (Exception e) {
                PALogUtil.defaultErrorInfo(logger, e);
                e.printStackTrace();
                continue;
            }
        }
        if (sysIds == null || sysIds.isEmpty()) {
            logger.info("初始化空文档向量模型！");
            try {
                long cost = InitBaseVecModel.initWordAndDocVecModel(CalcConstans.SINGLE_MODE_SYSID, new ArrayList<>(), defVecModelPath);
                logger.info("初始化系统id：{} 模型数据成功，耗时：{}ms", CalcConstans.SINGLE_MODE_SYSID, cost);
            } catch (Exception e) {
                PALogUtil.defaultErrorInfo(logger, e);
                e.printStackTrace();
            }
        }
    }

}
