package com.pingan.robot.calc.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word2vec.Vector;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.pingan.robot.calc.utils.HanLPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description 修正的文档向量模型
 * @Author chenxx
 * @Date 2019/3/6 17:29
 **/
public class FixDocVectorModel extends AbstractFixVectorModel<Integer> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final FixWordVectorModel wordVectorModel;
    private Segment segment;

    /**
     * 支持用户词典，但分词遵循分词模型并非一定显示词典中的分词
     *
     * @param wordVectorModel
     */
    public FixDocVectorModel(FixWordVectorModel wordVectorModel) {
        super();
        try {
            String segmentType = HanLPConfig.getConfig("docModelSegmentType");
            this.segment = HanLP.newSegment(segmentType).enableCustomDictionary(true);
            logger.info("启用配置的分词器：{}", segmentType);
        } catch (Exception e) {
            logger.info("启用配置的分词器失败，分配默认viterbi分词");
            this.segment = HanLP.newSegment("viterbi").enableCustomDictionary(true);
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
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(content);
        content = m.replaceAll("");
        if (content == null || content.length() == 0) return null;
        List<Term> termList;
        termList = NotionalTokenizer.segment(content);
        if ("1".equals(HanLPConfig.getConfig("isPrintSegWords")))
            logger.info(printTermList(content, termList));
        Vector result = new Vector(dimension());
        int n = 0;
        for (Term term : termList) {
            Vector vector = wordVectorModel.vector(term.word);
            if (vector == null) {
                logger.info("词向量模型不存在该词语--{}，请尝试加入扩展同义词词典", term.word);
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
