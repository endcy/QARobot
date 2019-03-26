package com.hanlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.junit.Test;

import java.util.List;

/**
 * @Description
 * @Author chenxx
 * @Date 2019/2/25 16:50
 **/
public class FunctionTest {
    private static String temp = "香港特别行政区的张朝阳说三原县鲁桥食品厂不好；旅行的美妙，不在于名胜古迹，而是“能有一段漂泊的时间”，随心所欲地到处走走，无所事事地发呆，" +
            "或是在小巷里迷路，看见当地居民为什么事而幸福快乐，你会确定自己想要追求什么样的人生！";

    /**
     * HanLP.segment其实是对StandardTokenizer.segment的包装。
     */
    @Test
    public void segment() {
        System.out.println(HanLP.segment(temp));
    }

    @Test
    public void standardSegment() {
        List<Term> termList = StandardTokenizer.segment(temp);
        System.out.println(termList);
    }

    @Test
    public void nlpSegment() {
        System.out.println(NLPTokenizer.segment(temp));
    }

    @Test
    public void indexSegment() {
        List<Term> termList = IndexTokenizer.segment(temp);
        for (Term term : termList) {
            System.out.println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]");
        }
    }

    @Test
    public void shortRootSegment() {
        Segment nShortSegment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        Segment shortestSegment = new DijkstraSegment().enableCustomDictionary(false).enablePlaceRecognize(true).enableOrganizationRecognize(true);
        String[] testCase = new String[]{
                "今天，刘志军案的关键人物,山西女商人丁书苗在市二中院出庭受审。",
                temp,
        };
        for (String sentence : testCase) {
            System.out.println("N-最短分词：" + nShortSegment.seg(sentence) + "\n最短路分词：" + shortestSegment.seg(sentence));
        }
    }

    @Test
    public void crfSegment() throws Exception {
        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
        String[] tests = new String[]{
                temp,
                "上海华安工业（集团）公司董事长谭旭光和秘书胡花蕊来到美国纽约现代艺术博物馆参观",
                "微软公司於1975年由比爾·蓋茲和保羅·艾倫創立，18年啟動以智慧雲端、前端為導向的大改組。" // 支持繁体中文
        };
        for (String sentence : tests) {
            System.out.println(analyzer.analyze(sentence));
        }
    }

    /**
     * 极速分词是词典最长分词，速度极其快，精度一般
     *
     * @throws Exception
     */
    @Test
    public void highSpeedSegment() throws Exception {
        String text = temp;
        System.out.println(SpeedTokenizer.segment(text));
        long start = System.currentTimeMillis();
        int pressure = 1000000;
        for (int i = 0; i < pressure; ++i) {
            SpeedTokenizer.segment(text);
        }
        double costTime = (System.currentTimeMillis() - start) / (double) 1000;
        System.out.printf("分词速度：%.2f字每秒", text.length() * pressure / costTime);
    }

    @Test
    public void autoSummary() throws Exception {
        List<String> sentenceList = HanLP.extractSummary(temp, 3);
        System.out.println(sentenceList);
    }

    @Test
    public void getKeywords() throws Exception {
        List<String> sentenceList = HanLP.extractKeyword(temp, 3);
        System.out.println(sentenceList);
    }

    @Test
    public void suggestSentenceByWord() {
        Suggester suggester = new Suggester();
        String[] titleArray =
                (
                        "威廉王子发表演说 呼吁保护野生动物\n" +
                                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                                "英报告说空气污染带来“公共健康危机”"
                ).split("\\n");
        for (String title : titleArray) {
            suggester.addSentence(title);
        }
        System.out.println(suggester.suggest("发言", 1));       // 语义
        System.out.println(suggester.suggest("危机公共", 1));   // 字符
        System.out.println(suggester.suggest("mayun", 1));      // 拼音
    }

    @Test
    public void dependencyParser() {
        CoNLLSentence sentence = HanLP.parseDependency(temp);
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence) {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--) {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null) {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
    }

    /**
     * demo代码确实
     */
    @Test
    public void word2Vec() {
    }


}
