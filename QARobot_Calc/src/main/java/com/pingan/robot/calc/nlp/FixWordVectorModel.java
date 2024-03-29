package com.pingan.robot.calc.nlp;


import com.hankcs.hanlp.mining.word2vec.Vector;

import java.io.IOException;
import java.util.*;

/**
 * @Description 修正的词向量工具
 * @Author chenxx
 * @Date 2019/3/14 11:31
 **/
public class FixWordVectorModel extends AbstractFixVectorModel<String> {
    /**
     * 加载模型<br>
     *
     * @param modelFileName 模型路径
     * @throws IOException 加载错误
     */
    public FixWordVectorModel(String modelFileName) throws IOException {
        super(loadVectorMap(modelFileName));
    }

    private static TreeMap<String, Vector> loadVectorMap(String modelFileName) throws IOException {
        FixVectorsReader reader = new FixVectorsReader(modelFileName);
        reader.readVectorFile();
        TreeMap<String, Vector> map = new TreeMap<String, Vector>();
        for (int i = 0; i < reader.vocab.length; i++) {
            map.put(reader.vocab[i], new Vector(reader.matrix[i]));
        }
        return map;
    }

    /**
     * 返回跟 A - B + C 最相似的词语,比如 中国 - 北京 + 东京 = 日本。输入顺序按照 中国 北京 东京
     *
     * @param A 做加法的词语
     * @param B 做减法的词语
     * @param C 做加法的词语
     * @return 与(A - B + C)语义距离最近的词语及其相似度列表
     */
    public List<Map.Entry<String, Float>> analogy(String A, String B, String C) {
        return analogy(A, B, C, 10);
    }

    /**
     * 返回跟 A - B + C 最相似的词语,比如 中国 - 北京 + 东京 = 日本。输入顺序按照 中国 北京 东京
     *
     * @param A    做加法的词语
     * @param B    做减法的词语
     * @param C    做加法的词语
     * @param size topN个
     * @return 与(A - B + C)语义距离最近的词语及其相似度列表
     */
    public List<Map.Entry<String, Float>> analogy(String A, String B, String C, int size) {
        Vector a = storage.get(A);
        Vector b = storage.get(B);
        Vector c = storage.get(C);
        if (a == null || b == null || c == null) {
            return Collections.emptyList();
        }

        List<Map.Entry<String, Float>> resultList = nearest(a.minus(b).add(c), size + 3);
        ListIterator<Map.Entry<String, Float>> listIterator = resultList.listIterator();
        while (listIterator.hasNext()) {
            String key = listIterator.next().getKey();
            if (key.equals(A) || key.equals(B) || key.equals(C)) {
                listIterator.remove();
            }
        }

        if (resultList.size() > size) {
            resultList = resultList.subList(0, size);
        }

        return resultList;
    }

    @Override
    public Vector query(String query) {
        return vector(query);
    }
}
