package com.pingan.robot.calc.service;

import com.hankcs.hanlp.suggest.Suggester;
import com.pingan.robot.calc.bean.SimpleAnsVO;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description 基于语义向量(同义词)+编辑距离+拼音匹配的三项（等权重）文本推荐
 * @Author chenxx
 * @Date 2019/3/26 11:30
 **/
@Component
public class ContentRecommService {
    //存储已存在的问题列表 线程安全
    private static Map<String, List<QAVO>> recommendList = new ConcurrentHashMap<>();

    @Autowired
    private IContentService contentService;

    /**
     * 根据word获取推荐
     *
     * @param word
     * @param client
     * @param sysId
     * @return
     */
    public List<QAVO> getRecommendByWord(String word, String client, int sysId) {
        List<QAVO> resList;
        Suggester suggester = new Suggester();
        List<QAVO> list = recommendList.get(client);
        if (list != null && list.size() > 0) {
            for (QAVO sentence : list)
                suggester.addSentence(sentence.getQuestion());
        } else {
            HashMap param = new HashMap();
            param.put("client", client);
            param.put("sysId", sysId);
            List<QAVO> qaList = contentService.findAll(param);
            for (QAVO vo : qaList)
                suggester.addSentence(vo.getQuestion());
            recommendList.put(client, qaList);
            list = qaList;
        }
        List<String> suggestStrs = suggester.suggest(word, 3);
        resList = findVObyString(suggestStrs, list);
        return resList;
    }

    /**
     * todo
     * 触发刷新recommendList列表
     */
    public void refreshList() {

    }

    private List<QAVO> findVObyString(List<String> strList, List<QAVO> ansVOList) {
        List<QAVO> result = new ArrayList<>();
        for (String str : strList) {
            for (QAVO vo : ansVOList) {
                if (vo.getQuestion().equals(str)) {
                    result.add(vo);
                }
            }
        }
        return result;
    }
}
