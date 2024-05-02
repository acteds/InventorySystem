package com.controller;

import com.aotmd.Translate;
import com.dao.MySql;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 初始化Bean
 * @author aotmd
 * @version 1.0
 * @date 2024/5/2 14:38
 */
@Controller
public class InitController implements BeanPostProcessor {
    private final MySql ms;
    private final ServletContext servletContext;

    public InitController(MySql ms, ServletContext servletContext) {
        this.ms = ms;
        this.servletContext = servletContext;
    }
    private boolean b=true;
    @Transactional
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        /*绑定在每个bean初始化后，只运行一次*/
        if (b){
            //设置翻译图,参数图,货品图,用户图
            servletContext.setAttribute("translate", Translate.getTranslate());
            servletContext.setAttribute("parameters", initializationParameters(ms));
            servletContext.setAttribute("goodsMap", initializationGoods(ms));
            servletContext.setAttribute("userMap", initializationUser(ms));
            b=false;
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    public static LinkedHashMap<String, String> initializationGoods(MySql mySql){
        mySql.setSql("select gid,name from goods");
        List<LinkedHashMap<String, Object>> list = mySql.runList();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (LinkedHashMap<String, Object> temp : list) {
            map.put(temp.get("gid").toString(), temp.get("name").toString());
        }
        return map;
    }

    public static LinkedHashMap<String, LinkedHashMap<String, String>> initializationParameters(MySql mySql){
        List<LinkedHashMap<String, Object>> result = mySql.setSql("select pmid from parameters_main").runList();
        int []pmids= new int[result.size()];
        for (int i=0;i<result.size();i++){
            pmids[i]= (int) result.get(i).get("pmid");
        }
        LinkedHashMap<String, LinkedHashMap<String, String>> parametersList = new LinkedHashMap<>();
        for (int pmid : pmids) {
            mySql.setSql("select name,value from parameters_sub where pmid=?").set(pmid);
            List<LinkedHashMap<String, Object>> list = mySql.runList();
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (LinkedHashMap<String, Object> temp : list) {
                map.put(temp.get("value").toString(), temp.get("name").toString());
            }
            parametersList.put(String.valueOf(pmid), map);
        }
        return parametersList;
    }

    public static LinkedHashMap<String, String> initializationUser(MySql mySql){
        mySql.setSql("select uid,name from user");
        List<LinkedHashMap<String, Object>> list = mySql.runList();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (LinkedHashMap<String, Object> temp : list) {
            map.put(temp.get("uid").toString(), temp.get("name").toString());
        }
        return map;
    }
}
