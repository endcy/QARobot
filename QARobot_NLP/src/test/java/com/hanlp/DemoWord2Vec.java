package com.hanlp;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/2/27 14:47
 **/

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Word2VecTrainer;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;

import java.io.IOException;
import java.util.Map;

/**
 * 演示词向量的训练与应用
 *
 * @author hankcs
 */
public class DemoWord2Vec {
    private static final String TRAIN_FILE_NAME = "C:/Users/admin/Desktop/NLP/搜狗文本分类语料库已分词.txt";
//    private static final String MODEL_FILE_NAME = "D:\\NLP\\vector\\hanlp-wiki-vec-zh.txt";
//    private static final String MODEL_FILE_NAME = "D:\\NLP\\vector\\vx_word2vec_c";
//    private static final String MODEL_FILE_NAME = "D:\\NLP\\vector\\fastText_wiki.zh.vec";
    private static final String MODEL_FILE_NAME = "D:\\NLP\\vector\\Tencent_AILab_ChineseEmbedding.txt";

    public static void main(String[] args) throws IOException {
        long s1 = System.currentTimeMillis();
        WordVectorModel wordVectorModel = trainOrLoadModel();
        long s2 = System.currentTimeMillis();
        System.out.println("加载模型" + MODEL_FILE_NAME + "耗时" + (s2 - s1));

        printNearest("表现", wordVectorModel);
        printNearest("异常", wordVectorModel);
        printNearest("加载", wordVectorModel);
//        printNearest("购买", wordVectorModel);
        long s3 = System.currentTimeMillis();
        System.out.println("模型内相似词匹配耗时" + (s3 - s2));

        // 文档向量
        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);
        //设定问题集
        String[] documents = new String[]{
                "为什么验证码总是提示更新失败",
                "界面显示下载验证码图片失败",
                "点击验证码的重试按钮无效",
                "手机的验证码显示异常",
                "商户如何接入最新的验证码接口",
        };
        for (String temp : documents) {
            System.out.println(temp);
        }
        System.out.println("比较第一和第二条及第一和第四条短句相似度……");
        System.out.println(docVectorModel.similarity(documents[0], documents[1]));
        System.out.println(docVectorModel.similarity(documents[0], documents[4]));
        long s4 = System.currentTimeMillis();
        System.out.println("文本相似计算耗时" + (s4 - s3));

        for (int i = 0; i < documents.length; i++) {
            docVectorModel.addDocument(i, documents[i]);
        }
        printNearestDocument("升级", documents, docVectorModel);
        printNearestDocument("提示验证码更新失败如何操作", documents, docVectorModel);
        printNearestDocument("验证失败后重试无效怎么办", documents, docVectorModel);
        printNearestDocument("苹果应用无法加载验证码", documents, docVectorModel);
        long s5 = System.currentTimeMillis();
        System.out.println("文本推荐耗时" + (s5 - s4));

    }

    static void printNearest(String word, WordVectorModel model) {
        printHeader(word);
        for (Map.Entry<String, Float> entry : model.nearest(word)) {
            System.out.printf("%50s\t\t%f\n", entry.getKey(), entry.getValue());
        }
    }

    static void printNearestDocument(String document, String[] documents, DocVectorModel model) {
        printHeader(document);
        for (Map.Entry<Integer, Float> entry : model.nearest(document)) {
            System.out.printf("%50s\t\t%f\n", documents[entry.getKey()], entry.getValue());
        }
    }

    private static void printHeader(String query) {
        System.out.printf("\n%50s          Cosine\n------------------------------------------------------------------------\n", query);
    }

    static WordVectorModel trainOrLoadModel() throws IOException {
        if (!IOUtil.isFileExisted(MODEL_FILE_NAME)) {
            if (!IOUtil.isFileExisted(TRAIN_FILE_NAME)) {
                System.err.println("语料不存在，请阅读文档了解语料获取与格式：https://github.com/hankcs/HanLP/wiki/word2vec");
                System.exit(1);
            }
            Word2VecTrainer trainerBuilder = new Word2VecTrainer();
            return trainerBuilder.train(TRAIN_FILE_NAME, MODEL_FILE_NAME);
        }

        return loadModel();
    }

    static WordVectorModel loadModel() throws IOException {
        return new WordVectorModel(MODEL_FILE_NAME);
    }
}