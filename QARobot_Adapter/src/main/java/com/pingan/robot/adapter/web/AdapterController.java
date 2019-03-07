package com.pingan.robot.adapter.web;

import com.pingan.robot.common.utils.StringUtil;
import com.pingan.robot.common.vo.QAVO;
import com.pingan.robot.data.bean.ResultVO;
import com.pingan.robot.data.dao.ICommonContDAO;
import com.pingan.robot.data.utils.ConstansDefination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/answer")
public class AdapterController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICommonContDAO commonContDAO;

    @CrossOrigin
    @RequestMapping(value = "/keyword")
    public List keyword(@RequestParam("content") String str) {
        String url = "http://QAROBOTCALCSERVICE/nlp/keyword/get?content={content}";
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("content", str);
        //利用@LoadBalanced注解，可在restTemplate里使用应用名称进行调用
        List list = restTemplate.getForObject(url, List.class, uriVariables);
        return list;
    }

    @CrossOrigin
    @RequestMapping(value = "/get")
    public ResultVO answer(@RequestParam("content") String str, @RequestParam("sysId") Integer sysId) {
        ResultVO resultVO = ResultVO.FAILURE();
        if (StringUtil.isEmpty(str)) {
            resultVO.setMsg("问题不能空！");
            return resultVO;
        }
        if (str.length() <= 3) {
            logger.info("触发关键词询问模式 keyword:{}", str);
            return recommend(str, sysId);  //todo ...
        }
        String url = "http://QAROBOTCALCSERVICE/calc/get?content={content}&sysId={sysId}";
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("content", str);
        uriVariables.put("sysId", sysId);
        //利用@LoadBalanced注解，可在restTemplate里使用应用名称进行调用
        List<QAVO> list = restTemplate.getForObject(url, List.class, uriVariables);
        resultVO = ResultVO.SUCCESS();
        if (list.isEmpty()) {
            resultVO.setMsg("很抱歉，未能找到相关问题或答案！") ;
            resultVO.setStatus(ConstansDefination.StatusCode[2]);
        } else if (list.size() == 1 && ConstansDefination.STANDARD_ANSWER.equals(((Map)list.get(0)).get("remark"))) {    //标准答案做了标记 todo list.get(0).getRemark() map转vo
            resultVO.setStatus(ConstansDefination.StatusCode[0]);
            resultVO.setObj(list);
        } else {
            resultVO.setMsg("未找到答案，如您想询问如下问题请直接点击");
            resultVO.setStatus(ConstansDefination.StatusCode[1]);
            resultVO.setObj(list);
        }
        return resultVO;
    }

    /**
     * 根据问题id查找答案
     *
     * @param id
     * @param sysId
     * @return
     */
    @CrossOrigin
    @RequestMapping(value = "/id/get")
    public ResultVO getAnswerById(@RequestParam("id") Integer id, @RequestParam("sysId") Integer sysId) {
        ResultVO resultVO = ResultVO.FAILURE();
        if (id < 1) {
            resultVO.setMsg("问题不能空！");
        }
        QAVO param = new QAVO();
        param.setId(id);
        param.setSysId(sysId);
        QAVO qavo = commonContDAO.find(param);
        if (qavo == null)
            resultVO.setMsg("id不存在");
        else {
            resultVO = ResultVO.SUCCESS();
            resultVO.setStatus(ConstansDefination.StatusCode[0]);
            List<QAVO> list = new ArrayList<>();
            list.add(qavo);
            resultVO.setObj(list);
            return resultVO;
        }
        return resultVO;
    }

    @CrossOrigin
    @RequestMapping(value = "/keyword/get")
    public ResultVO recommend(@RequestParam("content") String str, @RequestParam("sysId") Integer sysId) {
        ResultVO resultVO = ResultVO.FAILURE();
        String url = "http://QAROBOTCALCSERVICE/calc/recommend/get?keyword={keyword}&sysId={sysId}";
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("keyword", str);
        uriVariables.put("sysId", sysId);
        //利用@LoadBalanced注解，可在restTemplate里使用应用名称进行调用
        List<QAVO> list = restTemplate.getForObject(url, List.class, uriVariables);
        resultVO = ResultVO.SUCCESS();
        if (list.isEmpty()) {
            resultVO.setMsg("很抱歉，未能找到相关问题或答案！");
            resultVO.setStatus(ConstansDefination.StatusCode[2]);
        } else {
            resultVO.setMsg("未找到答案，如您想询问如下问题请直接点击");
            resultVO.setStatus(ConstansDefination.StatusCode[1]);
            resultVO.setObj(list);
        }
        return resultVO;
    }
}