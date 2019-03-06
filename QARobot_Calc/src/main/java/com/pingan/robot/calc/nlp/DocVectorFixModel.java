package com.pingan.robot.calc.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;

/**
 * @Description 更改分词为更准确的crf模式
 * @Author chenxx
 * @Date 2019/3/6 17:29
 **/
public class DocVectorFixModel extends DocVectorModel {

    private final WordVectorModel wordVectorModel;

    public DocVectorFixModel(WordVectorModel wordVectorModel) {
        super(wordVectorModel);
        this.wordVectorModel = wordVectorModel;
    }

    /**
     * 将一个文档转为向量
     *
     * @param content 文档
     * @return 向量
     */
    @Override
    public Vector query(String content) {
        if (content == null || content.length() == 0) return null;
        Segment segment = HanLP.newSegment("crf");
        List<Term> termList = segment.seg(content);
        Vector result = new Vector(dimension());
        int n = 0;
        for (Term term : termList) {
            Vector vector = wordVectorModel.vector(term.word);
            if (vector == null) {
                continue;
            }
            ++n;
            result.addToSelf(vector);
        }
        if (n == 0) {
            return null;
        }
        result.normalize();
        return result;
    }
}
