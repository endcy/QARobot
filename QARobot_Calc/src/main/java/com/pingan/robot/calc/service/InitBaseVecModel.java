package com.pingan.robot.calc.service;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.nlp.DocVectorFixModel;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 获取模型，使用加锁机制避免模型加载未完全生成就被调用
 * @Author chenxx
 * @Date 2019/3/5 16:14
 **/
public class InitBaseVecModel {

    private static ConcurrentHashMap<Integer, WordVectorModel> wordModelList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, DocVectorModel[]> docModelList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Boolean> docModelListStatus = new ConcurrentHashMap<>();
    public static AtomicBoolean initLock = new AtomicBoolean(false);

    /**
     * 给sysid的模型库初始化,可传入模型路径，为空则使用默认路径
     *
     * @param sysId
     * @param qaList
     * @param modelPath
     * @return
     * @throws Exception
     */
    public static long initWordAndDocVecModel(Integer sysId, List<QAVO> qaList, String modelPath) throws Exception {
//        String defModelPath = "D:\\NLP\\vector\\vx_word2vec_c";
        String defModelPath = "D:\\NLP\\vector\\hanlp-wiki-vec-zh.txt";
        docModelListStatus.put(sysId, false);
        long t1 = System.currentTimeMillis();
        if (StringUtil.isNotEmpty(modelPath))
            defModelPath = modelPath;
        WordVectorModel wordVecModel = new WordVectorModel(defModelPath);
        wordModelList.put(sysId, wordVecModel);
        DocVectorModel docVectorModel4Doc = new DocVectorModel(wordVecModel);
        DocVectorModel docVectorModel4Key = new DocVectorModel(wordVecModel);
        for (QAVO vo : qaList) {
            if (StringUtil.isNotEmpty(vo.getQuestion()))
                docVectorModel4Doc.addDocument(vo.getId(), vo.getQuestion());
            if (StringUtil.isNotEmpty(vo.getKeyword()))
                docVectorModel4Key.addDocument(vo.getId(), vo.getKeyword());
        }
        DocVectorModel[] docModel = new DocVectorModel[2];
        docModel[0] = docVectorModel4Doc;
        docModel[1] = docVectorModel4Key;
        docModelList.put(sysId, docModel);
        docModelListStatus.put(sysId, true);
        initLock.set(true);
        return System.currentTimeMillis() - t1;
    }

    /**
     * 词向量可能随系统不同而设定 后续设计
     *
     * @return
     */
    public static WordVectorModel getWordVecModel(Integer sysId) {
        if (initLock.get()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return wordModelList.get(sysId);
    }

    /**
     * 根据文档向量类型和系统id获取对应模型
     *
     * @param sysId
     * @param type
     * @return
     */
    public static DocVectorModel getDocVecModel(Integer sysId, DocVectorType type) {
        if (!initLock.get()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (docModelListStatus.get(sysId) != null && docModelListStatus.get(sysId)) {
            if (type == DocVectorType.Document)
                return docModelList.get(sysId)[0];
            else
                return docModelList.get(sysId)[1];
        }
        return null;
    }

}