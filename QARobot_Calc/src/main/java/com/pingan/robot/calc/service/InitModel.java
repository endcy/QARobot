package com.pingan.robot.calc.service;

import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.pingan.robot.calc.nlp.DocVectorFixModel;

import java.io.IOException;

/**
 * @Description 获取模型，使用同步避免模型加载未完全生成就被调用
 * @Author chenxx
 * @Date 2019/3/5 16:14
 **/
public class InitModel {

    private static WordVectorModel[] wordVectorModel;
    private static DocVectorModel[] docVectorModel;
    private static DocVectorFixModel[] docVectorFixModel;

    public static synchronized WordVectorModel getWordModel() {
        int i = 2;
        String MODEL_FILE_NAME = "D:\\NLP\\vector\\hanlp-wiki-vec-zh.txt";
        if (wordVectorModel == null || wordVectorModel.length < 1) {
            System.out.println("WordVectorModel 初始化… setNum" + i);
            wordVectorModel = new WordVectorModel[2];
            while (i > 0) {
                WordVectorModel wordVecModel = null;
                try {
                    wordVecModel = new WordVectorModel(MODEL_FILE_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                wordVectorModel[i - 1] = wordVecModel;
                i--;
            }
        }
        int radom = (int) (Math.random() * i);
        return wordVectorModel[radom];//随机给个模型
    }

    public static synchronized DocVectorFixModel getDocFixModel() {
        int i = 2;
        if (docVectorFixModel == null || docVectorFixModel.length < 1) {
            System.out.println("DocVectorFixModel 初始化… setNum" + i);
            docVectorFixModel = new DocVectorFixModel[2];
            while (i > 0) {
                DocVectorFixModel docVecModel = null;
                docVecModel = new DocVectorFixModel(getWordModel());
                docVectorFixModel[i - 1] = docVecModel;
                i--;
            }
        }
        int radom = (int) (Math.random() * i);
        return docVectorFixModel[radom];//随机给个模型
    }

    public static synchronized DocVectorModel getDocModel() {
        int i = 2;
        if (docVectorModel == null || docVectorModel.length < 1) {
            System.out.println("DocVectorModel 初始化… setNum" + i);
            docVectorModel = new DocVectorModel[2];
            while (i > 0) {
                DocVectorModel docVecModel = null;
                docVecModel = new DocVectorModel(getWordModel());
                docVectorModel[i - 1] = docVecModel;
                i--;
            }
        }
        int radom = (int) (Math.random() * i);
        return docVectorModel[radom];//随机给个模型
    }

}
