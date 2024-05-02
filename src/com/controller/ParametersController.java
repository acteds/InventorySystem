package com.controller;

import com.aotmd.Tools;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 参数控制器
 * @author aotmd
 * @version 1.0
 * @date 2023/3/20 15:42
 */
@Controller
public class ParametersController {
    private final MySql ms;

    /**
     * 初始化参数字典
     * @param ms dao
     */
    public ParametersController(MySql ms) {
        this.ms = ms;
    }

    @RequestMapping("/parametersMainList")
    public String parametersMainList(HttpServletRequest request) {
        //----------------------------------------判断传值并写入session------------------------------------------------
        ms.setSql("SELECT pm.*,(SELECT count(ps.pmid) FROM parameters_sub ps WHERE pm.pmid=ps.pmid) as ParametersSubCount " +
                "from parameters_main pm order by pmid asc limit ?,?");
        ms.runPagination(request, "/parametersMainList", 10);
        request.setAttribute("top", ms.getTop());
        //----------------------------------转发------------------------------------------------------------
        return "parametersMainList";
    }
    @RequestMapping("/parametersMainInsert")
    public String parametersMainInsert(HttpServletRequest request){
        ms.setSql("select * from parameters_main").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"pmid");
        request.getSession().setAttribute("top",top);
        return "parametersMainInsert";
    }
    @RequestMapping("/ParametersMainInsert")
    public void ParametersMainInsert(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("select * from parameters_main where name=?").set(name);
        if(!ms.runList().isEmpty()) {
            response.getWriter().print("<script>alert('添加参数名称失败,名称重复');window.location='parametersMainList'</script>");
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
            response.getWriter().print("<script>alert('添加参数名称失败');window.location='parametersMainList'</script>");
        }
    }
    @RequestMapping("/parametersMainChange")
    public String parametersMainChange(HttpServletRequest request,String pmid){
        ms.setSql("select * from parameters_main where pmid=?").set(pmid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        request.getSession().setAttribute("top",ms.getTop());
        request.getSession().setAttribute("list",list);
        return "parametersMainChange";
    }
    @RequestMapping("/ParametersMainChange")
    public void ParametersMainChange(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        HttpSession session=request.getSession();
        LinkedHashMap<String, Object> list=((LinkedHashMap<String, Object>) session.getAttribute("list"));
        // ---------------------------------------查重-----------------------------
        //原名字
        String name0= list.get("name").toString();
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
        ms.set(Integer.parseInt(list.get("pmid").toString()));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=parametersMainList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='parametersMainChange'</script>");
        }
    }
    @RequestMapping("/ParametersMainDel")
    public void ParametersMainDel(HttpServletResponse response, String pmid) throws IOException {
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
        if (ms.run() > 0) {
            response.setHeader("refresh", "0;URL=parametersMainList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='parametersMainList'</script>");
        }
    }
    @RequestMapping("/parametersSubList")
    public String parametersSubList(HttpServletRequest request, String pmid, String name) {
        HttpSession session = request.getSession();
        //----------------------------------------判断传值并写入session------------------------------------------------
        if (pmid!=null&&name!=null){
            session.setAttribute("parametersMainID",pmid);
            session.setAttribute("parametersMainName",name);
        }else {
            pmid= (String) session.getAttribute("parametersMainID");
        }
        ms.setSql("SELECT psid,name,value,explanation FROM parameters_sub where pmid=? order by value asc limit ?,?");
        ms.set(Integer.parseInt(pmid));
        ms.runPagination(request, "/parametersSubList", 10);
        String []top=ms.getTop();
        top=Tools.delString(top,"psid");
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
        request.getSession().setAttribute("top",top);
        return "parametersSubInsert";
    }
    @Transactional
    @RequestMapping("/ParametersSubInsert")
    public void ParametersSubInsert(HttpServletRequest request, HttpServletResponse response,String name,String value) throws IOException {
        int pmid= Integer.parseInt((String) request.getSession().getAttribute("parametersMainID"));
        // ---------------------------------------查重-----------------------------
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("SELECT * FROM parameters_sub where pmid=? and( name=? or value=?)").set(pmid).set(name).set(value);
        if(!ms.runList().isEmpty()) {
            response.getWriter().print("<script>alert('添加键值失败,名称或值重复，请重新输入');window.history.go(-1);</script>");
            return;
        }

        ms.setSql("insert into parameters_sub value(0,?,?,?,?)").set(pmid);
        for (String temp : top) {
            String s = request.getParameter(temp);
            ms.set(s);
        }
        if(ms.run()>0) {
            /*更新图*/
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("parameters", InitController.initializationParameters(ms));
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('添加参数失败');window.location='parametersSubList'</script>");
        }
    }
    @RequestMapping("/parametersSubChange")
    public String parametersSubChange(HttpServletRequest request,String psid){
        ms.setSql("select * from parameters_sub where psid=?").set(psid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        request.getSession().setAttribute("top",ms.getTop());
        request.getSession().setAttribute("list",list);
        return "parametersSubChange";
    }
    @Transactional
    @RequestMapping("/ParametersSubChange")
    public void ParametersSubChange(HttpServletRequest request, HttpServletResponse response,String name,String value) throws IOException {
        HttpSession session=request.getSession();
        LinkedHashMap<String, Object> list=((LinkedHashMap<String, Object>) session.getAttribute("list"));
        // ---------------------------------------查重-----------------------------
        int psid= Integer.parseInt(list.get("psid").toString());
        int pmid= Integer.parseInt((String) session.getAttribute("parametersMainID"));
        ms.setSql("SELECT * FROM parameters_sub where pmid=? and psid !=? and ( name=? or value=? )")
                .set(pmid).set(psid).set(name).set(value);
        ms.runList();
        //有记录
        if (ms.getSum()!= 0) {
            response.getWriter().print("<script>alert('修改键值失败，名称或值已存在，请重新输入');window.history.go(-1);</script>");
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
        if(ms.run()>0) {
            /*更新图*/
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("parameters", InitController.initializationParameters(ms));
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('修改键值失败');window.history.go(-1);</script>");
        }
    }
    @Transactional
    @RequestMapping("/ParametersSubDel")
    public void ParametersSubDel(HttpServletRequest request, HttpServletResponse response,String psid) throws IOException {
        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (psid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='parametersSubList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM parameters_sub where psid=?").set(Integer.parseInt(psid));
        if (ms.run() > 0) {
            /*更新图*/
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("parameters", InitController.initializationParameters(ms));
            response.setHeader("refresh", "0;URL=parametersSubList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='parametersSubList'</script>");
        }
    }
}
