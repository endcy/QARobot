package com.pingan.robot.calc.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.pingan.robot.calc.utils.HanLPConfig;

import java.util.List;
import java.util.Map;

/**
 * @Description 文档向量模型
 * @Author chenxx
 * @Date 2019/3/6 17:29
 **/
public class FixDocVectorModel extends AbstractFixVectorModel<Integer> {
    private final WordVectorModel wordVectorModel;
    private Segment segment;

    public FixDocVectorModel(WordVectorModel wordVectorModel) {
        super();
        try {
            String segmentType = HanLPConfig.getConfig("docModelSegmentType");
            this.segment = HanLP.newSegment(segmentType);
        } catch (Exception e) {
            this.segment = HanLP.newSegment("viterbi");
        }
        synchronized (NotionalTokenizer.class) {
            NotionalTokenizer.SEGMENT = segment;
        }
        this.wordVectorModel = wordVectorModel;
    }

    /**
     * 添加文档
     *
     * @param id      文档id
     * @param content 文档内容
     * @return 文档向量
     */
    public Vector addDocument(int id, String content) {
        Vector result = query(content);
        if (result == null) return null;
        storage.put(id, result);
        return result;
    }


    /**
     * 查询最相似的前10个文档
     *
     * @param query 查询语句（或者说一个文档的内容）
     * @return
     */
    public List<Map.Entry<Integer, Float>> nearest10Doc(String query) {
        return queryNearest(query, 10);
    }

    /**
     * 将一个文档转为向量
     *
     * @param content 文档
     * @return 向量
     */
    public Vector query(String content) {
        if (content == null || content.length() == 0) return null;
        List<Term> termList;
        termList = NotionalTokenizer.segment(content);
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

    /**
     * 返回文档的词向量的维数
     *
     * @return
     */
    @Override
    public int dimension() {
        return wordVectorModel.dimension();
    }

    /**
     * 文档相似度计算
     *
     * @param what
     * @param with
     * @return
     */
    public float similarity(String what, String with) {
        Vector A = query(what);
        if (A == null) return -1f;
        Vector B = query(with);
        if (B == null) return -1f;
        return A.cosineForUnitVector(B);
    }

}
