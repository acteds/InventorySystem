package com.controll;

import com.aotmd.Tools;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * 入库管理
 * @author aotmd
 * @version 1.0
 * @date 2023/3/23 15:40
 */
@Controller
public class InventoryAddController {
    private MySql ms;

    public InventoryAddController(MySql ms) {
        this.ms = ms;
    }

    @RequestMapping("/inventoryAddList")
    public String inventoryAddList(HttpServletRequest request) {
        LinkedHashMap<String, Object> user= (LinkedHashMap<String, Object>) request.getSession().getAttribute("user");
        int rank=Integer.parseInt((String) user.get("rank"));
        if (rank==1){
            ms.setSql("SELECT * FROM inventory where status=0 order by iid desc limit ?,?");
        }else {
            ms.setSql("SELECT * FROM inventory where uid=? and review<=10 and status=0 order by iid desc limit ?,?");
            ms.set(Integer.parseInt(user.get("uid").toString()));
        }
        ms.runPagination(request, "/inventoryAddList", 10);
        String []top=ms.getTop();
        if (rank!=1){
            top=Tools.delString(top,"uid");
        }
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"explanation");
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "inventoryAddList";
    }
    @RequestMapping("/inventoryAddInsert")
    public String inventoryAddInsert(HttpServletRequest request){
        ms.setSql("select * from inventory").runList();
        String []top=ms.getTop();
        top= Tools.delString(top,"iid");
        top= Tools.delString(top,"uid");
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"createTime");
        top= Tools.delString(top,"review");
        request.getSession().setAttribute("top",top);
        return "inventoryAddInsert";
    }
    @RequestMapping("/InventoryAddInsert")
    public void InventoryAddInsert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int uid= (int) ((LinkedHashMap<String, Object>) request.getSession().getAttribute("user")).get("uid");
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("insert into inventory(gid, quantity, location, explanation, createTime, uid) value(?,?,?,?,?,?)");
        for (String temp : top) {
            String s = request.getParameter(temp);
            ms.set(s);
        }
        ms.set(new Timestamp(new Date().getTime()));
        ms.set(uid);
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=inventoryAddList");
        } else {
            response.getWriter().print("<script>alert('入库失败');window.location='inventoryAddList'</script>");
        }
    }
    @RequestMapping("/inventoryAddChange")
    public String inventoryAddChange(HttpServletRequest request,String iid){
        ms.setSql("select * from inventory where iid=? and status=0").set(iid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"uid");
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"review");

        request.getSession().setAttribute("top",top);
        request.getSession().setAttribute("list",list);
        return "inventoryAddChange";
    }
    @RequestMapping("/InventoryAddChange")
    public void InventoryAddChange(HttpServletRequest request, HttpServletResponse response,String iid) throws IOException {
        HttpSession session=request.getSession();
        ms.setSql("select * from inventory where review>=10 and iid=? and status=0").set(Integer.parseInt(iid));
        if (ms.runList().size()>0){
            response.getWriter().print("<script>alert('已经审核通过了,无法修改.');window.history.go(-1);</script>");
            return;
        }

        // --------------------------------------------修改----------------------------------------------------
        //若审核失败可以通过修改内容重新进入审核状态.
        ms.setSql("UPDATE inventory SET gid=?,quantity=?,location=?,explanation=?,review=0 WHERE iid=? and status=0");
        String[]top=(String[]) session.getAttribute("top");
        top= Tools.delString(top,"createTime");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(Integer.parseInt(iid));
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=inventoryAddList");
            return;
        }
        response.getWriter().print("<script>alert('修改失败');window.history.go(-1);</script>");
    }
    @RequestMapping("/inventoryAddDel")
    public void inventoryAddDel(HttpServletResponse response, int iid) throws IOException {
        //查询审核状态
        ms.setSql("select review from inventory where iid=? and status=0").set(iid);
        int review=Integer.parseInt(ms.runList().get(0).get("review").toString());
        if (review>=10){
            response.getWriter().print("<script>alert('已经审核通过了,无法删除.');window.location='inventoryAddList';</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM inventory where iid=? and status=0").set(iid);
        if (ms.run() > 0) {
            //一并删除审核记录
            ms.setSql("DELETE FROM inventory_review where iid=?").set(iid);
            ms.run();
            response.setHeader("refresh", "0;URL=inventoryAddList");
            return;
        }
        response.getWriter().print("<script>alert('删除失败');window.location='inventoryAddList';</script>");
    }

    /**
     * 审核模块
     * @param request
     * @return
     */
    @RequestMapping("/inventoryReviewAddList")
    public String inventoryReviewAddList(HttpServletRequest request) {
        LinkedHashMap<String, Object> user= (LinkedHashMap<String, Object>) request.getSession().getAttribute("user");
        int rank=Integer.parseInt((String) user.get("rank"));
        if (rank==1){
            ms.setSql("SELECT * FROM inventory where status=0 order by review asc limit ?,?");
        }else {
            ms.setSql("SELECT * FROM inventory where review<10 and status=0 order by review asc limit ?,?");
        }
        ms.runPagination(request, "/inventoryReviewAddList", 10);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"explanation");
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "inventoryReviewAddList";
    }
    @RequestMapping("/inventoryReviewAddReview")
    public String inventoryReviewAddReview(HttpServletRequest request,String iid){
        ms.setSql("select * from inventory_review where iid=?").set(Integer.parseInt(iid));
        /*如果有审核记录则此次请求为修改*/
        if (ms.runList().size()>0){
            ms.setSql("select i.*,ir.explanation as explanation2 " +
                    "from inventory i,inventory_review ir " +
                    "where i.iid=ir.iid and i.iid=?").set(Integer.parseInt(iid));
        }else {
            ms.setSql("select * from inventory where iid=?").set(Integer.parseInt(iid));
        }

        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");

        HttpSession session=request.getSession();
        session.setAttribute("top",top);
        session.setAttribute("list",list);
        session.setAttribute("iid",iid);
        return "inventoryReviewAddReview";
    }
    @RequestMapping("/InventoryReviewAddReview")
    public void inventoryReviewAddReview(HttpServletRequest request, HttpServletResponse response,String review,String explanation2) throws IOException {
        HttpSession session=request.getSession();
        int iid=Integer.parseInt((String) session.getAttribute("iid"));
        int uid=Integer.parseInt(((LinkedHashMap<String,Object>) session.getAttribute("user")).get("uid").toString());
        ms.setSql("select * from inventory where review>=10 and iid=?").set(iid);
        if (ms.runList().size()>0){
            response.getWriter().print("<script>alert('已经审核通过了,无法修改.');window.history.go(-1);</script>");
            return;
        }

        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("select * from inventory_review where iid=?").set(iid);
        /*如果有审核记录则此次请求为修改*/
        if (ms.runList().size()>0){
            ms.setSql("UPDATE inventory_review SET explanation=? where iid=?").set(explanation2).set(iid);
        }else {
            ms.setSql("INSERT INTO inventory_review VALUE(0,?,?,?)").set(iid).set(uid).set(explanation2);
        }
        if(ms.run()>0) {
            ms.setSql("UPDATE inventory SET review=? WHERE iid=?").set(Integer.parseInt(review)).set(iid);
            if(ms.run()>0) {
                response.setHeader("refresh", "0;URL=inventoryReviewAddList");
            } else {
                response.getWriter().print("<script>alert('修改失败');window.history.go(-1);</script>");
            }
        } else {
            response.getWriter().print("<script>alert('修改失败');window.history.go(-1);</script>");
        }
    }
    @RequestMapping("/inventoryReviewAddInfo")
    public String inventoryReviewAddInfo(HttpServletRequest request,String iid){
        ms.setSql("select i.*,ir.explanation as explanation2,ir.uid as uid2 " +
                "from inventory i,inventory_review ir " +
                "where i.iid=ir.iid and i.iid=?").set(Integer.parseInt(iid));
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");

        HttpSession session=request.getSession();
        session.setAttribute("top",top);
        session.setAttribute("list",list);
        session.setAttribute("iid",iid);
        return "inventoryReviewAddInfo";
    }
    @RequestMapping("/InventoryReviewAddInfo")
    public void InventoryReviewAddInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        int iid=Integer.parseInt((String) session.getAttribute("iid"));
        ms.setSql("select review from inventory where iid=?").set(iid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        /*审核通过确认*/
        if (Integer.parseInt(list.get("review").toString())==10){
            ms.setSql("update inventory set review=11 where iid=?").set(iid);
            if(ms.run()>0) {
                response.setHeader("refresh", "0;URL=inventoryAddList");
            } else {
                response.getWriter().print("<script>alert('确认消息失败');window.history.go(-1);</script>");
            }
        }else {
            response.setHeader("refresh", "0;URL=inventoryAddList");
        }
    }
}
