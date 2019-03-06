package com.pingan.robot.data.web;

import com.pingan.robot.common.log.PALogUtil;
import com.pingan.robot.common.utils.DESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBHelperController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 对部分特殊字符如 / 等无转义支持
     *
     * @param str
     * @return
     */
    @RequestMapping(value = "/encrypt/{content}")
    public String getEncPsw(@PathVariable("content") String str) {
        String afterEnc = null;
        try {
            afterEnc = DESUtil.encryptStr(str);
        } catch (Exception e) {
            PALogUtil.defaultErrorInfo(logger, e);
            return "error:" + e.getMessage();
        }
        return afterEnc;
    }

    @RequestMapping(value = "/encrypt")
    public String getEncPswX(@RequestParam("content") String str) {
        return getEncPsw(str);
    }

    @RequestMapping(value = "/decrypt/{content}")
    public String getDecPsw(@PathVariable("content") String str) {
        String afterDec = null;
        try {
            afterDec = DESUtil.decryptStr(str);
        } catch (Exception e) {
            PALogUtil.defaultErrorInfo(logger, e);
            return "error:" + e.getMessage();
        }
        return afterDec;
    }

    @RequestMapping(value = "/decrypt")
    public String getDecPswY(@RequestParam("content") String str) {
        return getDecPsw(str);
    }

}
