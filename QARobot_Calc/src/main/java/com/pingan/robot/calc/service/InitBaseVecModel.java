package com.pingan.robot.calc.service;

import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.pingan.robot.calc.bean.DocVectorType;
import com.pingan.robot.calc.nlp.FixDocVectorModel;
import com.pingan.robot.calc.nlp.FixWordVectorModel;
import com.pingan.robot.calc.utils.CalcConstans;
import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description 获取模型，使用加锁机制避免模型加载未完全生成就被调用
 * @Author chenxx
 * @Date 2019/3/5 16:14
 **/
public class InitBaseVecModel {
    private static Logger logger = LoggerFactory.getLogger(InitBaseVecModel.class);
    //    private static ConcurrentHashMap<Integer, WordVectorModel> wordModelList = new ConcurrentHashMap<>();
    private static FixWordVectorModel wordVectorModel;
    private static ConcurrentHashMap<Integer, FixDocVectorModel[]> docModelList = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, Boolean> docModelListStatus = new ConcurrentHashMap<>();
    private static AtomicBoolean initLock = new AtomicBoolean(false);

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
        docModelListStatus.put(sysId, false);
        long t1 = System.currentTimeMillis();
        if (StringUtil.isEmpty(modelPath))
            modelPath = CalcConstans.DEF_MODEL_PATH;
        FixWordVectorModel wordVecModel = null;
        if (wordVectorModel == null) {
            wordVectorModel = new FixWordVectorModel(modelPath);
        }
        wordVecModel = wordVectorModel;
//        wordModelList.put(sysId, wordVecModel);
        FixDocVectorModel docVectorModel4Doc = new FixDocVectorModel(wordVecModel);
        FixDocVectorModel docVectorModel4Key = new FixDocVectorModel(wordVecModel);
        for (QAVO vo : qaList) {
            if (StringUtil.isNotEmpty(vo.getQuestion()))
                docVectorModel4Doc.addDocument(vo.getId(), vo.getQuestion());
            if (StringUtil.isNotEmpty(vo.getKeyword()))
                docVectorModel4Key.addDocument(vo.getId(), vo.getKeyword());
        }
        FixDocVectorModel[] docModel = new FixDocVectorModel[2];
        docModel[0] = docVectorModel4Doc;
        docModel[1] = docVectorModel4Key;
        docModelList.put(sysId, docModel);
        docModelListStatus.put(sysId, true);
        /**
         * 接下来初始化默认的空文档向量
         */
        if (docModelListStatus.get(CalcConstans.SINGLE_MODE_SYSID) == null || !docModelListStatus.get(CalcConstans.SINGLE_MODE_SYSID)) {
//            wordModelList.put(CalcConstans.SINGLE_MODE_SYSID, wordVecModel);
            FixDocVectorModel docSingleModel4Doc = new FixDocVectorModel(wordVecModel);
            //初始化配置配置文件词典模型之类的 避免实际初次调用卡顿
            docSingleModel4Doc.similarity("a", "b");
            FixDocVectorModel[] docSingleModel = new FixDocVectorModel[1];
            docSingleModel[0] = docSingleModel4Doc;
            docModelList.put(CalcConstans.SINGLE_MODE_SYSID, docSingleModel);
            docModelListStatus.put(CalcConstans.SINGLE_MODE_SYSID, true);
        }

        initLock.set(true);
        return System.currentTimeMillis() - t1;
    }

    /**
     * 词向量可能随系统不同而设定
     *
     * @return
     */
    public static FixWordVectorModel getWordVecModel(Integer sysId) {
        if (!initLock.get()) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
//        return wordModelList.get(sysId);
        return wordVectorModel;
    }

    /**
     * 根据文档向量类型和系统id获取对应模型
     *
     * @param sysId
     * @param type
     * @return
     */
    public static FixDocVectorModel getDocVecModel(Integer sysId, DocVectorType type) {
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
        logger.info("模型未就绪，请稍后重试！");
        return null;
    }

    /**
     * 加载自定义同义词到词向量模型
     *
     * @param syns
     */
    public static void addCustomSynoWord2Model(List<String[]> syns) {
        if (wordVectorModel == null) {
            logger.info("模型未就绪，请稍后重试！");
            return;
        }
        synchronized (wordVectorModel) {
            for (String[] strs : syns) {
                if (strs.length < 2)
                    return;
                //找出已存在的基础向量，记录位置
                Vector base = null;
                int baseIdx = -1;
                for (String str : strs) {
                    baseIdx++;
                    if (StringUtil.isEmpty(str))
                        break;
                    if (!str.contains("+")) {
                        if (base == null)
                            base = wordVectorModel.vector(str);
                        //if (base != null) break;    //找到存在的任意一条词向量就记录作为基础向量--fix--逻辑错误，只要存在X=Y+Z的形式，Y+Z组合词条就必作为基础向量
                    } else {
                        base = new Vector(wordVectorModel.dimension());
                        String[] simVec = str.split("\\+");     //否则组合词条作为基础向量，组合词中任一一个词不存在则失败处理
                        for (int i = 0; i < simVec.length; i++) {
                            Vector temp = wordVectorModel.vector(simVec[i]);
                            //存在一个为空则该组合式不成立
                            if (temp == null) {
                                base = null;
                                break;
                            }
//                            if (i == 0) base = temp;
//                            else base = base.add(temp);
                            base.addToSelf(temp);
                        }
                        if (base != null)
                            base.normalize();
                        break;
                    }
                }
                if (base == null && strs.length > 0) {
                    logger.info(strs[0] + "...该条同义词向量导入失败");
                    continue;
                }
                //二次循环 加入向量模型
                for (int j = 0; j < strs.length; j++) {
                    if (j != baseIdx && !strs[j].contains("+"))
                        wordVectorModel.addVector(strs[j], base);
                }

            }
        }
        logger.info("导入自定义同义词词向量完成！");
    }
}
