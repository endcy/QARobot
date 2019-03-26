package com.hanlp;

import com.hankcs.hanlp.HanLP;
import org.junit.Test;

import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/25 10:18
 **/
public class TextRankTest {
    @Test
    public void getSummary() {
        String document = "为什么没有查询到我的寿险信息呢，难道我交的平安是假的";
        List<String> sentenceList = HanLP.extractSummary(document, 1);
        List<String> keywordList = HanLP.extractKeyword(document, 5);
        List<String> phraseList = HanLP.extractPhrase(document, 3);
        System.out.println(sentenceList.toString());
    }
}
