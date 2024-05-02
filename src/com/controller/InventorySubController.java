package com.controller;

import com.aotmd.Tools;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author aotmd
 * @version 1.0
 * @date 2023/3/27 14:36
 */
@Controller
public class InventorySubController {
    private final MySql ms;

    public InventorySubController(MySql ms) {
        this.ms = ms;
    }

    @RequestMapping("/inventorySubList")
    public String inventorySubList(HttpServletRequest request) {
        LinkedHashMap<String, Object> user= (LinkedHashMap<String, Object>) request.getSession().getAttribute("user");
        int rank=Integer.parseInt((String) user.get("rank"));
        if (rank==1){
            ms.setSql("SELECT * FROM inventory where status!=0 order by iid desc limit ?,?");
        }else {
            ms.setSql("SELECT * FROM inventory where uid=? and review<=10 and status!=0 order by iid desc limit ?,?");
            ms.set(Integer.parseInt(user.get("uid").toString()));
        }
        ms.runPagination(request, "/inventorySubList", 10);
        String []top=ms.getTop();
        if (rank!=1){
            top= Tools.delString(top,"uid");
        }
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"explanation");
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "inventorySubList";
    }
    @RequestMapping("/inventorySubInsertList")
    public String inventorySubInsertList(HttpServletRequest request) {
        ms.setSql("SELECT gid,sum(quantity) as quantity FROM inventory " +
                "where review>=10 and status=0 GROUP BY gid order by gid asc limit ?,?");
        ms.runPagination(request, "/inventorySubInsertList", 10);
        String []top=ms.getTop();
        request.setAttribute("top", top);
        return "inventorySubInsertList";
    }
    @RequestMapping("/inventorySubInsertListInfo")
    public String inventorySubInsertListInfo(HttpServletRequest request,String gid) {
        LinkedHashMap<String, String> goods=(LinkedHashMap<String, String>) request.getSession().getServletContext().getAttribute("goodsMap");
        ms.setSql("SELECT * FROM inventory where gid=? and review>=10 and status=0 order by createTime desc limit ?,?");
        ms.set(Integer.parseInt(gid));
        ms.runPagination(request, "/inventorySubInsertListInfo", 10);
        String []top=ms.getTop();
        top=Tools.delString(top,"review");
        top=Tools.delString(top,"gid");
        top=Tools.delString(top,"status");
        top=Tools.delString(top,"explanation");
        String goodsName=goods.get(gid);
        request.setAttribute("top", top);
        request.setAttribute("goodsName", goodsName);
        return "inventorySubInsertListInfo";
    }
    @RequestMapping("/inventorySubInsert")
    public String inventorySubInsert(HttpServletRequest request,String iid){
        ms.setSql("select * from inventory where iid=? and review>=10").set(Integer.parseInt(iid));
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"review");

        request.getSession().setAttribute("top",top);
        request.getSession().setAttribute("list",list);
        request.getSession().setAttribute("iid",iid);
        return "inventorySubInsert";
    }
    @RequestMapping("/InventorySubInsert")
    public void InventorySubInsert(HttpServletRequest request, HttpServletResponse response,int quantity2,String explanation2) throws IOException {
        int uid= Integer.parseInt (((LinkedHashMap<String, Object>) request.getSession().getAttribute("user")).get("uid").toString());
        int iid=Integer.parseInt ((request.getSession().getAttribute("iid").toString()));
        LinkedHashMap<String, Object> list= (LinkedHashMap<String, Object>) request.getSession().getAttribute("list");
        int gid=Integer.parseInt(list.get("gid").toString());
        String location=list.get("location").toString();
        /*核对是否超过最大值,查对应入库单库存*/
        ms.setSql("select quantity from inventory where iid=? and review>=10").set(iid);
        int quantity=Integer.parseInt(ms.runList().get(0).get("quantity").toString());
        if (quantity2>quantity){
            response.getWriter().print("<script>alert('添加失败,超过最大值');window.history.go(-1);</script>");
            return;
        }

        ms.setSql("insert into inventory(gid, uid, quantity, location, explanation, status, createTime) value(?,?,?,?,?,?,?)");
        ms.set(gid).set(uid).set(quantity2).set(location).set(explanation2).set(iid);
        ms.set(new Timestamp(new Date().getTime()));
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=inventorySubList");
        } else {
            response.getWriter().print("<script>alert('出库清单添加失败');window.location='inventorySubList'</script>");
        }
    }
    @RequestMapping("/inventorySubChange")
    public String inventorySubChange(HttpServletRequest request, HttpServletResponse response,String iid) throws IOException {
        HttpSession session = request.getSession();
        session.setAttribute("iid",iid);
        /*查出库数据*/
        ms.setSql("select quantity,status,explanation,createTime from inventory where iid=? and review<10 and status!=0").set(iid);
        LinkedHashMap<String, Object> list2=ms.runList().get(0);
        if (ms.getSum()==0){
            response.getWriter().print("<script>alert('已经审核通过了,无法修改.');window.location='inventorySubList';</script>");
            return "inventorySubList";
        }
        String []top2=ms.getTop();
        top2= Tools.delString(top2,"status");
        session.setAttribute("top2",top2);
        session.setAttribute("list2",list2);
        /*查对应入库单库存*/
        int status = Integer.parseInt(list2.get("status").toString());
        session.setAttribute("status",status);
        ms.setSql("select * from inventory where iid=? and review>=10 and status=0").set(status);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"review");

        session.setAttribute("top",top);
        session.setAttribute("list",list);

        return "inventorySubChange";
    }
    @RequestMapping("/InventorySubChange")
    public void InventorySubChange(HttpServletRequest request, HttpServletResponse response,int quantity2,String explanation2) throws IOException {
        HttpSession session=request.getSession();
        int iid=Integer.parseInt(session.getAttribute("iid").toString());
        /*查出库单的审核状态*/
        ms.setSql("select * from inventory where review>=10 and iid=? and status!=0").set(iid);
        if (!ms.runList().isEmpty()){
            response.getWriter().print("<script>alert('已经审核通过了,无法修改.');window.location='inventorySubList';</script>");
            return;
        }
        int status=Integer.parseInt(session.getAttribute("status").toString());
        /*查对应入库单库存*/
        ms.setSql("select quantity from inventory where iid=? and review>=10 and status=0").set(status);
        int quantity=Integer.parseInt(ms.runList().get(0).get("quantity").toString());
        if (quantity2>quantity){
            response.getWriter().print("<script>alert('修改失败,超过最大值');window.history.go(-1);</script>");
            return;
        }
        //todo 考虑添加限制:若当前正在审核的出库加上本次的数量大于最大值,则拦截这次添加.
        // --------------------------------------------修改----------------------------------------------------
        //若审核失败可以通过修改内容重新进入审核状态.
        ms.setSql("UPDATE inventory SET quantity=?,explanation=?,review=0 WHERE iid=? and status!=0");
        ms.set(quantity2).set(explanation2).set(iid);
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=inventorySubList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.history.go(-1);</script>");
        }
    }
    @Transactional
    @RequestMapping("/inventorySubDel")
    public void inventorySubDel(HttpServletResponse response, int iid) throws IOException {
        //查询审核状态
        ms.setSql("select review from inventory where iid=? and status!=0").set(iid);
        List<LinkedHashMap<String, Object>> list = ms.runList();
        if (ms.getSum()==0){
            response.getWriter().print("<script>alert('已经被删除了,无法删除.');window.location='inventorySubList';</script>");
            return;
        }
        int review=Integer.parseInt(list.get(0).get("review").toString());
        if (review>=10){
            response.getWriter().print("<script>alert('已经审核通过了,无法删除.');window.location='inventorySubList';</script>");
            return;
        }
        //----------------------------------------删除-------------------------------------------------------------------
        ms.setSql("DELETE FROM inventory where iid=? and status!=0").set(iid);
        if (ms.run() > 0) {
            //一并删除审核记录
            ms.setSql("DELETE FROM inventory_review where iid=?").set(iid);
            ms.run();
            response.setHeader("refresh", "0;URL=inventorySubList");
            return;
        }
        response.getWriter().print("<script>alert('删除失败');window.location='inventorySubList'</script>");
    }
    /**
     * 审核模块
     */
    @RequestMapping("/inventoryReviewSubList")
    public String inventoryReviewSubList(HttpServletRequest request) {
        LinkedHashMap<String, Object> user= (LinkedHashMap<String, Object>) request.getSession().getAttribute("user");
        int rank=Integer.parseInt((String) user.get("rank"));
        if (rank==1){
            ms.setSql("SELECT * FROM inventory where status!=0 order by review asc limit ?,?");
        }else {
            ms.setSql("SELECT * FROM inventory where review<10 and status!=0 order by review asc limit ?,?");
        }
        ms.runPagination(request, "/inventoryReviewSubList", 10);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"explanation");
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "inventoryReviewSubList";
    }
    @RequestMapping("/inventoryReviewSubReview")
    public String inventoryReviewSubReview(HttpServletRequest request,int iid){
        HttpSession session = request.getSession();
        session.setAttribute("iid",iid);
        /*---------------------------------查出库数据---------------------------------*/
        ms.setSql("select * from inventory where iid=? and status!=0").set(iid);
        LinkedHashMap<String, Object> inventorySub=ms.runList().get(0);
        String []top2=ms.getTop();
        top2= Tools.delString(top2,"gid");
        top2= Tools.delString(top2,"location");
        top2= Tools.delString(top2,"status");
        session.setAttribute("top2",top2);
        session.setAttribute("inventorySub",inventorySub);

        /*---------------------------------查对应入库单库存---------------------------------*/
        int status = Integer.parseInt(inventorySub.get("status").toString());
        //留到提交时查询数量用
        session.setAttribute("status",status);
        ms.setSql("select * from inventory where iid=? and review>=10 and status=0").set(status);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"review");

        session.setAttribute("top",top);
        session.setAttribute("list",list);

        /*---------------------------------查审核记录---------------------------------*/
        ms.setSql("select explanation from inventory_review where iid=?").set(iid);
        /*如果有审核记录则此次请求为修改*/
        List<LinkedHashMap<String, Object>> list3=ms.runList();
        if (ms.getSum()>0){
            session.setAttribute("inventory_review",list3.get(0).get("explanation"));
        }
        return "inventoryReviewSubReview";
    }
    @Transactional
    @RequestMapping("/InventoryReviewSubReview")
    public void inventoryReviewSubReview(HttpServletRequest request, HttpServletResponse response,int review,String explanation) throws IOException {
        HttpSession session=request.getSession();
        int iid= (int) session.getAttribute("iid");
        int uid=Integer.parseInt(((LinkedHashMap<String,Object>) session.getAttribute("user")).get("uid").toString());
        ms.setSql("select * from inventory where review>=10 and iid=?").set(iid);
        if (!ms.runList().isEmpty()){
            response.getWriter().print("<script>alert('已经审核通过了,无法修改.');window.location.href='inventoryReviewSubList';</script>");
            return;
        }
        int status= (int) session.getAttribute("status");
        /*校验库存与出库数量*/
        ms.setSql("select quantity from inventory where iid=? and review>=10 and status=0").set(status);
        int quantity=Integer.parseInt(ms.runList().get(0).get("quantity").toString());
        ms.setSql("select quantity from inventory where iid=? and status!=0").set(iid);
        int subQuantity=Integer.parseInt(ms.runList().get(0).get("quantity").toString());
        if (subQuantity>quantity){
            response.getWriter().print("<script>alert('审核失败,出库数量大于库存');window.location.href='inventoryReviewSubList';</script>");
            return;
        }

        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("select * from inventory_review where iid=?").set(iid);
        /*如果有审核记录则此次请求为修改*/
        if (!ms.runList().isEmpty()){
            ms.setSql("UPDATE inventory_review SET explanation=? where iid=?").set(explanation).set(iid);
        }else {
            ms.setSql("INSERT INTO inventory_review VALUE(0,?,?,?)").set(iid).set(uid).set(explanation);
        }
        if(ms.run()<=0) {
            response.getWriter().print("<script>alert('审核记录修改失败');window.history.go(-1);</script>");
            return;
        }

        ms.setSql("UPDATE inventory SET review=? WHERE iid=?").set(review).set(iid);
        if(ms.run()<=0) {
            response.getWriter().print("<script>alert('出库单记录修改失败');window.history.go(-1);</script>");
            return;
        }
        /*审核未通过不修改库存*/
        if (review>=10){
            ms.setSql("UPDATE inventory SET quantity=quantity-? WHERE iid=?").set(subQuantity).set(status);
            if (ms.run()<=0) {
                response.getWriter().print("<script>alert('入库单记录修改失败');window.history.go(-1);</script>");
                return;
            }
            //todo 考虑数量变为0则删除记录,或在查询加where字段屏蔽掉
        }
        response.setHeader("refresh", "0;URL=inventoryReviewSubList");
    }
    @RequestMapping("/inventoryReviewSubInfo")
    public String inventoryReviewSubInfo(HttpServletRequest request,String iid){
        HttpSession session = request.getSession();
        session.setAttribute("iid",iid);
        /*---------------------------------查出库数据---------------------------------*/
        ms.setSql("select * from inventory where iid=? and status!=0").set(iid);
        LinkedHashMap<String, Object> inventorySub=ms.runList().get(0);
        String []top2=ms.getTop();
        top2= Tools.delString(top2,"gid");
        top2= Tools.delString(top2,"location");
        top2= Tools.delString(top2,"status");
        session.setAttribute("top2",top2);
        session.setAttribute("inventorySub",inventorySub);

        /*---------------------------------查对应入库单库存---------------------------------*/
        int status = Integer.parseInt(inventorySub.get("status").toString());
        //留到提交时查询数量用
        session.setAttribute("status",status);
        ms.setSql("select * from inventory where iid=? and review>=10 and status=0").set(status);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        String []top=ms.getTop();
        top= Tools.delString(top,"status");
        top= Tools.delString(top,"review");

        session.setAttribute("top",top);
        session.setAttribute("list",list);

        /*---------------------------------查审核记录---------------------------------*/
        ms.setSql("select * from inventory_review where iid=?").set(iid);
        session.setAttribute("inventory_review",ms.runList().get(0));
        return "inventoryReviewSubInfo";
    }
    @RequestMapping("/InventoryReviewSubInfo")
    public void InventoryReviewSubInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        int iid=Integer.parseInt((String) session.getAttribute("iid"));
        ms.setSql("select review from inventory where iid=?").set(iid);
        LinkedHashMap<String, Object> list=ms.runList().get(0);
        /*审核通过确认*/
        if (Integer.parseInt(list.get("review").toString())==10){
            ms.setSql("update inventory set review=11 where iid=?").set(iid);
            if(ms.run()>0) {
                response.setHeader("refresh", "0;URL=inventorySubList");
            } else {
                response.getWriter().print("<script>alert('确认消息失败');window.history.go(-1);</script>");
            }
        }else {
            response.setHeader("refresh", "0;URL=inventorySubList");
        }
    }
}
