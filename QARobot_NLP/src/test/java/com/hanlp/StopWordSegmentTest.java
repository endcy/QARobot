package com.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import org.junit.Test;

import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/3/13 9:48
 **/
public class StopWordSegmentTest {

    @Test
    public void segment() {
        String content = "为什么官网能登陆，却认证不了？答：您的登陆信息可能和您注册的信息不相符或账户中没有关联保单信息，您可以拨打百年人寿的客服电话进行保单关联。百年人寿的全国统一客户服务专线：95542。";
        List<Term> termList0 = NotionalTokenizer.segment(content);
        printWordByList("NotionalTokenizer default", termList0);
        List<Term> termList3 = HanLP.newSegment("viterbi").seg(content);
        printWordByList("Viterbi default", termList3);
        List<Term> termList1 = NotionalTokenizer.SEGMENT.enableCustomDictionary(true).seg(content);
        printWordByList("Viterbi enableCustomDictionary", termList1);
        List<Term> termList2 = NotionalTokenizer.SEGMENT.enableCustomDictionaryForcing(true).seg(content);
        printWordByList("Viterbi enableCustomDictionaryForcing", termList1);
        List<Term> termList4 = HanLP.newSegment("crf").seg(content);
        printWordByList("crf default", termList4);
        List<Term> termList5 = HanLP.newSegment("crf").enableCustomDictionary(true).seg(content);
        printWordByList("crf enableCustomDictionary", termList5);
        List<Term> termList6;
        Segment segment1 = HanLP.newSegment("viterbi").enableCustomDictionary(true);
        synchronized (NotionalTokenizer.class) {
            NotionalTokenizer.SEGMENT = segment1;
            termList6 = NotionalTokenizer.segment(content);
        }
        printWordByList("viterbi enableCustomDictionary in NotionalTokenizer", termList6);
        List<Term> termList7;
        Segment segment2 = HanLP.newSegment("crf").enableCustomDictionary(true);
        synchronized (NotionalTokenizer.class) {
            NotionalTokenizer.SEGMENT = segment2;
            termList7 = NotionalTokenizer.segment(content);
        }
        printWordByList("crf enableCustomDictionary in NotionalTokenizer", termList7);
        System.out.println();

    }

    public void printWordByList(String type, List<Term> list) {
        StringBuffer buffer = new StringBuffer(type + " --size:" + list.size() + ": \n");
        for (Term term : list) {
            buffer.append(term.word).append("\t");
        }
        System.out.println(buffer.toString());
    }
}
