package com.pingan.robot.data.service.impl;

import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import com.pingan.robot.data.service.IContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/8 15:36
 **/
@Repository
@Transactional
public class ContentSevice implements IContentService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ICommonContDAO commonContDAO;

    @Override
    public int insert(QAVO content) {
        if (content.getId() == null || content.getSysId() == null) {
            logger.error("insert check error! info:{}", content.toString());
            return 0;
        }
        return commonContDAO.insert(content);
    }

    @Override
    public int update(QAVO content) {
        if (content.getId() == null || content.getSysId() == null) {
            logger.error("update check error! info:{}", content.toString());
            return 0;
        }
        return commonContDAO.update(content);
    }

    @Override
    public int delete(QAVO content) {
        if (content.getId() == null || content.getSysId() == null) {
            logger.error("delete check error! info:{}", content.toString());
            return 0;
        }
        return commonContDAO.delete(content);
    }

    @Override
    public QAVO find(QAVO content) {
        if (content.getId() == null || content.getSysId() == null) {
            logger.error("find check error! info:{}", content.toString());
            return null;
        }
        return commonContDAO.find(content);
    }

    @Override
    public List<QAVO> findAll(HashMap params) {
        //支持keyword
        return commonContDAO.findAll(params);
    }

    @Override
    public HashMap<Integer, QAVO> findAllMap(HashMap params) {
        List<QAVO> list = findAll(params);
        HashMap<Integer, QAVO> map = new HashMap<>(list.size());
        for (QAVO vo : list)
            map.put(vo.getId(), vo);
        return map;
    }

    @Override
    public List<Integer> findAllSysId() {
        return commonContDAO.findAllSysId();
    }


}
