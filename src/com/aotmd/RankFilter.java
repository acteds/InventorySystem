package com.aotmd;

import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aotmd
 * @version 1.0
 * @date 2023/4/19 21:00
 */
@Order(2)
public class RankFilter implements HandlerInterceptor {
    private Map<String, int[]> rankMap;
    private Map<String, String> excludeURLs;
    private void init(){
        /*权限控制*/
        rankMap=new HashMap<>();
        rankMap.put("/publicInventory",new int[]{1,2,3,4,5,6,7});
        rankMap.put("/inventoryAdd",new int[]{1,2});
        rankMap.put("/inventorySub",new int[]{1,3});
        rankMap.put("/inventoryReviewAdd",new int[]{1,4});
        rankMap.put("/inventoryReviewSub",new int[]{1,5});
        rankMap.put("/user",new int[]{1,6});
        rankMap.put("/goods",new int[]{1,7});
        rankMap.put("/parameters",new int[]{1});

        /*排除拦截的网址*/
        String[] excludeURLs = { "/UserChange", "/userChange"};
        this.excludeURLs =new HashMap<>();
        for (String s : excludeURLs) {
            this.excludeURLs.put(s, s);
        }
    }
    public RankFilter() {
        init();
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*URL网址*/
        String requestUri = request.getRequestURI();
        /*项目名称*/
        String ctxPath = request.getContextPath();
        /*实际控制器Url*/
        String controllerUri = requestUri.substring(ctxPath.length());
        /*排除拦截的网址*/
        if (excludeURLs.get(controllerUri)!=null) {
            System.out.print("R网址排除 ");
            return true;
        }
        for (String s : rankMap.keySet()) {
            if (controllerUri.toLowerCase().contains(s.toLowerCase())) {
                int[] arr=rankMap.get(s);
                int rank= Integer.parseInt(((LinkedHashMap<String, Object>) request.getSession().getAttribute("user")).get("rank").toString());
                for (int value : arr) {
                    if (value == rank) {
                        System.out.print("R已授权 ");
                        return true;
                    }
                }
                response.getWriter().print("<script>alert('未授权,权限不足!');window.location='main'</script>");
                System.out.print("R未授权 ");
                return false;
            }
        }
        return true;
    }
}
