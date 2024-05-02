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
import java.util.LinkedHashMap;

/**
 * 货货品信息
 * @author aotmd
 * @version 1.0
 * @date 2023/3/22 12:30
 */
@Controller
public class GoodsController {
    private final MySql ms;

    /**
     * 初始化货品信息
     * @param ms dao
     */
    public GoodsController(MySql ms) {
        this.ms = ms;
    }

    @RequestMapping("/goodsList")
    public String goodsList(HttpServletRequest request) {
        ms.setSql("SELECT * FROM goods order by gid asc limit ?,?");
        ms.runPagination(request, "/goodsList", 10);
        String []top=ms.getTop();
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "goodsList";
    }
    @RequestMapping("/goodsInsert")
    public String goodsInsert(HttpServletRequest request){
        ms.setSql("select * from goods").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"gid");
        request.getSession().setAttribute("top",top);
        return "goodsInsert";
    }
    @Transactional
    @RequestMapping("/GoodsInsert")
    public void GoodsInsert(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        // ---------------------------------------查重-----------------------------
        ms.setSql("SELECT * FROM goods where name=?").set(name);
        if(!ms.runList().isEmpty()) {
            response.getWriter().print("<script>alert('添加货品类别失败,名称重复，请重新输入');window.history.go(-1);</script>");
            return;
        }
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("insert into goods value(0,?,?)");
        for (String temp : top) {
            String s = request.getParameter(temp);
            ms.set(s);
        }
        if(ms.run()>0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("goods", InitController.initializationGoods(ms));
            response.setHeader("refresh", "0;URL=goodsList");
        } else {
            response.getWriter().print("<script>alert('添加货品类别失败');window.location='goodsList'</script>");
        }
    }
    @RequestMapping("/goodsChange")
    public String goodsChange(HttpServletRequest request,String gid){
        ms.setSql("select * from goods where gid=?").set(gid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        request.getSession().setAttribute("top",ms.getTop());
        request.getSession().setAttribute("list",list);
        return "goodsChange";
    }
    @Transactional
    @RequestMapping("/GoodsChange")
    public void GoodsChange(HttpServletRequest request, HttpServletResponse response,String name) throws IOException {
        HttpSession session=request.getSession();
        LinkedHashMap<String, Object> list=((LinkedHashMap<String, Object>) session.getAttribute("list"));
        // ---------------------------------------查重-----------------------------
        int gid= Integer.parseInt(list.get("gid").toString());
        ms.setSql("SELECT * FROM goods where gid !=? and name=?").set(gid).set(name).runList();
        //有记录
        if (ms.getSum()!= 0) {
            response.getWriter().print("<script>alert('名称已存在，请重新输入');window.history.go(-1);</script>");
            return;
        }
        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("UPDATE goods SET name=?,explanation=? WHERE gid=?");
        String[]top=(String[]) session.getAttribute("top");
        top=Tools.delString(top,"gid");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(gid);
        if(ms.run()>0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("goods", InitController.initializationGoods(ms));
            response.setHeader("refresh", "0;URL=goodsList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.history.go(-1);</script>");
        }
    }
    @Transactional
    @RequestMapping("/GoodsDel")
    public void GoodsDel(HttpServletRequest request, HttpServletResponse response,String gid) throws IOException {

        //-----------------------------------判断是否有传值-----------------------------------------------------------------
        if (gid == null) {
            response.getWriter().print("<script>alert('非法访问没有传值');window.location='goodsList'</script>");
            return;
        }
        ms.setSql("select * from inventory where gid=?").set(Integer.parseInt(gid));
        if (!ms.runList().isEmpty()){
            response.getWriter().print("<script>alert('删除失败,该货品类别已经被使用');window.location='goodsList'</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM goods where gid=?").set(Integer.parseInt(gid));
        if (ms.run() > 0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("goods", InitController.initializationGoods(ms));
            response.setHeader("refresh", "0;URL=goodsList");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='goodsList'</script>");
        }
    }
}
