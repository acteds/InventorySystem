package com.controll;

import com.aotmd.Tools;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * @author 参数控制器
 * @version 1.0
 * @date 2023/3/20 15:42
 */
@Controller
public class ParametersController {
    private MySql ms;

    /**
     * 初始化参数字典
     * @param ms dao
     */
    public ParametersController(MySql ms) {
        this.ms = ms;
    }
    public static LinkedHashMap<String, LinkedHashMap<String, String>> initializationParameters(MySql mySql){
        List<LinkedHashMap<String, Object>> result = mySql.setSql("select pmid from parameters_main").runList();
        int []pmid= new int[result.size()];
        for (int i=0;i<result.size();i++){
            pmid[i]= (int) result.get(i).get("pmid");
        }
        LinkedHashMap<String, LinkedHashMap<String, String>> parametersList = new LinkedHashMap<>();
        for (int i = 0; i < pmid.length; i++) {
            mySql.setSql("select name,value from parameters_sub where pmid=?").set(pmid[i]);
            List<LinkedHashMap<String, Object>> list = mySql.runList();
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (LinkedHashMap<String, Object> temp : list) {
                map.put(temp.get("value").toString(), temp.get("name").toString());
            }
            parametersList.put(String.valueOf(pmid[i]), map);
        }
        return parametersList;
    }
    @RequestMapping("/parametersMainList")
    public String parametersMainList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        //noinspection unchecked ---------------是否管理员-----------------------------------------------------------
        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
        if (!("1".equals(user.get("uid").toString()))) {
            response.getWriter().print("<script>alert('你不是管理员');</script>");
            return null;
        }
        //----------------------------------------判断传值并写入session------------------------------------------------

        ms.setSql("SELECT pm.*,(SELECT count(ps.pmid) FROM parameters_sub ps WHERE pm.pmid=ps.pmid) as ParametersSubCount " +
                "from parameters_main pm order by pmid asc limit ?,?");
        ms.runPagination(request, "/parametersMainList", 10);
        request.setAttribute("sum", ms.getSum());
        request.setAttribute("top", ms.getTop());
        //----------------------------------转发------------------------------------------------------------
        return "parametersMainList";
    }
    @RequestMapping("/parametersMainInsert")
    public String parametersMainInsert(HttpServletRequest request){
        ms.setSql("select * from parameters_main").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"pmid");
        System.out.println(Arrays.toString(top));
        request.getSession().setAttribute("top",top);
        return "parametersMainInsert";
    }
    @RequestMapping("/ParametersMainInsert")
    public void ParametersMainInsert(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("select * from parameters_main where name=?").set(name);
        if(ms.runList().size()>0) {
            response.getWriter().print("<script>alert('添加参数失败,名称重复');window.location='parametersMainList'</script>");
            return;
        }
        ms.setSql("insert into parameters_main value(0,?,?)");
        for (String value : top) {
            String s = request.getParameter(value);
            ms.set(s);
        }
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=parametersMainList");
        } else {
            response.getWriter().print("<script>alert('添加参数失败');window.location='parametersMainList'</script>");
        }
    }
    @RequestMapping("/parametersMainChange")
    public String parametersMainChange(HttpServletRequest request,String pmid){
        ms.setSql("select * from parameters_main where pmid=?").set(pmid);
        LinkedHashMap<String, Object> parametersMain=ms.runList().get(0);
        request.getSession().setAttribute("top",ms.getTop());
        request.getSession().setAttribute("parametersMain",parametersMain);
        return "parametersMainChange";
    }
    @RequestMapping("/ParametersMainChange")
    public void ParametersMainChange(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        HttpSession session=request.getSession();
        LinkedHashMap<String, Object> parametersMain=((LinkedHashMap<String, Object>) session.getAttribute("parametersMain"));
        // ---------------------------------------查重-----------------------------
        //原名字
        String name0= parametersMain.get("name").toString();
        ms.setSql("SELECT * FROM parameters_main where name=?").set(name).runList();
        //有记录且原名字与新名字不同
        if (ms.getSum()!= 0&&!(name0.equals(name))) {
            response.getWriter().print("<script>alert('名称已存在，请重新输入');window.location='parametersMainChange'</script>");
            return;
        }
        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("UPDATE parameters_main SET name=?,explanation=? WHERE pmid=?");
        String[]top=(String[]) session.getAttribute("top");
        top=Tools.delString(top,"pmid");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(Integer.parseInt(parametersMain.get("pmid").toString()));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=parametersMainList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='parametersMainChange'</script>");
        }
    }
    @RequestMapping("/ParametersMainDel")
    public void ParametersMainDel(HttpServletRequest request, HttpServletResponse response,String pmid) throws IOException {
        HttpSession session = request.getSession();
        //noinspection unchecked --------------是否管理员--------------------------------------------------------
//        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
//        if (!("超级管理员".equals(user.get("rank").toString()))) {
//            response.getWriter().print("<script>alert('你不是管理员');window.location='parametersMainList'</script>");
//            return;
//        }
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (pmid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='parametersMainList'</script>");
            return;
        }
        //--------------------------------再次判断参数数量为0(防止开发者工具删除js代码)---------------------------------
        int size=ms.setSql("select * from parameters_sub where pmid=?").set(Integer.parseInt(pmid)).runList().size();
        if (size>0) {
            response.getWriter().print("<script>alert('参数不为0,无法删除');window.location='parametersMainList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM parameters_main where pmid=?").set(Integer.parseInt(pmid));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=parametersMainList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='parametersMainList'</script>");
        }
    }
    @RequestMapping("/parametersSubList")
    public String parametersSubList(HttpServletRequest request, HttpServletResponse response,String pmid,String name) {
        HttpSession session = request.getSession();
        //noinspection unchecked ---------------是否管理员-----------------------------------------------------------
//        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
//        if (!("1".equals(user.get("uid").toString()))) {
//            response.getWriter().print("<script>alert('你不是管理员');</script>");
//            return null;
//        }
        //----------------------------------------判断传值并写入session------------------------------------------------
        if (pmid!=null&&name!=null){
            session.setAttribute("parametersMainID",pmid);
            session.setAttribute("parametersMainName",name);
        }else {
            pmid= (String) session.getAttribute("parametersMainID");
        }
        ms.setSql("SELECT psid,name,value,explanation FROM parameters_sub where pmid=? order by psid asc limit ?,?");
        ms.set(Integer.parseInt(pmid));
        ms.runPagination(request, "/parametersSubList", 10);
        String []top=ms.getTop();
        top=Tools.delString(top,"psid");
        request.setAttribute("sum", ms.getSum());
        request.setAttribute("top", top);

        //----------------------------------转发------------------------------------------------------------
        return "parametersSubList";
    }
    @RequestMapping("/parametersSubInsert")
    public String parametersSubInsert(HttpServletRequest request){
        ms.setSql("select * from parameters_sub").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"pmid");
        top=Tools.delString(top,"psid");
        System.out.println(Arrays.toString(top));
        request.getSession().setAttribute("top",top);
        return "parametersSubInsert";
    }
    @RequestMapping("/ParametersSubInsert")
    public void ParametersSubInsert(HttpServletRequest request, HttpServletResponse response,String name,String value) throws IOException {
        // ---------------------------------------查重-----------------------------
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("SELECT * FROM parameters_sub where name=? or value=?").set(name).set(value);
        if(ms.runList().size()>0) {
            response.getWriter().print("<script>alert('添加参数失败,名称或值重复，请重新输入');window.history.go(-1);</script>");
            return;
        }

        int pmid= Integer.parseInt((String) request.getSession().getAttribute("parametersMainID"));
        ms.setSql("insert into parameters_sub value(0,?,?,?,?)").set(pmid);
        for (String temp : top) {
            String s = request.getParameter(temp);
            ms.set(s);
        }
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('添加参数失败');window.location='parametersSubList'</script>");
        }
    }
    @RequestMapping("/parametersSubChange")
    public String parametersSubChange(HttpServletRequest request,String psid){
        ms.setSql("select * from parameters_sub where psid=?").set(psid);
        LinkedHashMap<String, Object> parametersSub=ms.runList().get(0);
        request.getSession().setAttribute("top",ms.getTop());
        request.getSession().setAttribute("parametersSub",parametersSub);
        return "parametersSubChange";
    }
    @RequestMapping("/ParametersSubChange")
    public void ParametersSubChange(HttpServletRequest request, HttpServletResponse response,String name,String value) throws IOException {
        HttpSession session=request.getSession();
        LinkedHashMap<String, Object> parametersSub=((LinkedHashMap<String, Object>) session.getAttribute("parametersSub"));
        // ---------------------------------------查重-----------------------------
        int psid= Integer.parseInt(parametersSub.get("psid").toString());
        ms.setSql("SELECT * FROM parameters_sub where psid !=? and ( name=? or value=? )").set(psid).set(name).set(value);
        List<LinkedHashMap<String, Object>> result = ms.runList();
        //有记录
        if (ms.getSum()!= 0) {
            response.getWriter().print("<script>alert('名称或值已存在，请重新输入');window.history.go(-1);</script>");
            return;
        }
        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("UPDATE parameters_sub SET name=?,value=?,explanation=? WHERE psid=?");
        String[]top=(String[]) session.getAttribute("top");
        top=Tools.delString(top,"pmid");
        top=Tools.delString(top,"psid");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(psid);
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.history.go(-1);");
        }
    }
    @RequestMapping("/ParametersSubDel")
    public void ParametersSubDel(HttpServletRequest request, HttpServletResponse response,String psid) throws IOException {
        HttpSession session = request.getSession();
        //noinspection unchecked --------------是否管理员--------------------------------------------------------
//        Map<String, Object> user = (Map<String, Object>) session.getAttribute("user");
//        if (!("超级管理员".equals(user.get("rank").toString()))) {
//            response.getWriter().print("<script>alert('你不是管理员');window.location='parametersMainList'</script>");
//            return;
//        }
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (psid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='parametersSubList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM parameters_sub where psid=?").set(Integer.parseInt(psid));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='parametersSubList'</script>");
        }
    }
}