package com.utils;

import java.io.*;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/2/28 8:59
 **/
public class ReadBigTextLine {
    public static String filepath1 = "D:\\NLP\\vector\\fastText_wiki.zh.vec";
    public static String filepath2 = "D:\\NLP\\vector\\hanlp-wiki-vec-zh.txt";
    public static String filepath3 = "D:\\NLP\\vector\\polyglot-zh.txt";
    public static String filepath4 = "D:\\NLP\\vector\\sougou_word2vec.txt";
    public static String filepath5 = "D:\\NLP\\vector\\Tencent_AILab_ChineseEmbedding.txt";
    public static String filepath6 = "D:\\NLP\\vector\\vx_word2vec_c";
    public static String filepath7 = "D:\\NLP\\vector\\sgns.baidubaike.bigram-char";

    public static void main(String[] args) throws Exception {

        File file = new File(filepath7);
        BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
        int count = 10;
        String line = "";
        while ((line = reader.readLine()) != null && count > 0) {
            System.out.println(line);
            count--;
        }
        reader.close();
        fis.close();
    }
}
