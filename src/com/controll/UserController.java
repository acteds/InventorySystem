package com.controll;

import com.aotmd.Tools;
import com.aotmd.Translate;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 用户控制器
 *
 * @author aotmd
 */
@Controller
public class UserController {
    /**数据库操作类*/
    private MySql ms;
    public UserController(MySql ms) {this.ms = ms;}
    public static LinkedHashMap<String, String> initializationUser(MySql mySql){
        mySql.setSql("select uid,name from user");
        List<LinkedHashMap<String, Object>> list = mySql.runList();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (LinkedHashMap<String, Object> temp : list) {
            map.put(temp.get("uid").toString(), temp.get("name").toString());
        }
        return map;
    }
    @RequestMapping("/main")
    public String main(){
        return "main";
    }
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        ServletContext application= request.getSession().getServletContext();
        //设置翻译图,参数图,货品图,用户图
        application.setAttribute("translate", Translate.getTranslate());
        application.setAttribute("parameters",ParametersController.initializationParameters(ms));
        application.setAttribute("goodsMap",GoodsController.initializationGoods(ms));
        application.setAttribute("userMap",UserController.initializationUser(ms));
        return "login";
    }
    @RequestMapping("/Login")
    public String login(HttpServletRequest request, HttpServletResponse response, String username, String password) throws IOException {
        ms.setSql("select * from user where name=? and password=? and status=1").set(username).set(password);
        List<LinkedHashMap<String, Object>> list =ms.runList();
        if (list.size()==0){
            response.getWriter().print("<script>alert('登录失败');window.location='login'</script>");
            return null;
        }else {
            request.getSession().setAttribute("user",list.get(0));
            return "redirect:main";
        }
    }
    @RequestMapping("/Logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:login";
    }
    @RequestMapping("/userInsert")
    public String userInsert(HttpServletRequest request){
        ms.setSql("select * from user").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"uid");
        top=Tools.delString(top,"status");
        request.getSession().setAttribute("top",top);
        return "userInsert";
    }
    @RequestMapping("/UserInsert")
    public void UserInsert(HttpServletRequest request, HttpServletResponse response,String username) throws IOException {
        //用户管理员无法赋予超级管理员权限.
        HttpSession session=request.getSession();
        int rank=Integer.parseInt(request.getParameter("rank"));
        if(rank==1 &&Integer.parseInt(((LinkedHashMap<String, Object>) session.getAttribute("user")).get("rank").toString())==6){
            response.getWriter().print("<script>alert('你无权添加超级管理员权限');window.location='userList'</script>");
            return;
        }
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("select * from user where name=?").set(username);
        if(ms.runList().size()>0) {
            response.getWriter().print("<script>alert('添加用户失败,名称重复');window.location='userInsert'</script>");
            return;
        }
        ms.setSql("insert into user value(0,?,?,?,?,1)");
        for (String value : top) {
            String s = request.getParameter(value);
            ms.set(s);
        }
        if(ms.run()>0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("userMap",UserController.initializationUser(ms));
            response.setHeader("refresh", "0;URL=userList");
        } else {
            response.getWriter().print("<script>alert('添加用户失败');window.location='userInsert'</script>");
        }
    }
    @RequestMapping("/userChange")
    public String userChange(HttpServletRequest request){
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        ms.setSql("select * from user where uid=?").set(user.get("uid"));
        ms.runList();
        String [] top=Tools.delString(ms.getTop(),"uid");
        top=Tools.delString(top,"status");
        request.getSession().setAttribute("top",top);
        return "userChange";
    }
    @RequestMapping("/UserChange")
    public void userChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        //noinspection unchecked
        LinkedHashMap<String, Object> user=(LinkedHashMap<String,Object>)session.getAttribute("user");
        //----------------------------------------旧密码正确-------------------------------------------
        //用户登录时记录的密码
        String password0=user.get("password").toString();
        //用户表单提交的旧密码
        String password=request.getParameter("password0");
        if(!(password0.equals(password))) {
            response.getWriter().print("<script>alert('原密码错误');window.location='userChange'</script>");
            return;
        }
        // ---------------------------------------查重(并可避免修改账号时登录其他人的账号与密码)-----------------------------
        //用户修改后的登录名字
        String name=request.getParameter("name");
        //用户原登录名字
        ms.setSql("SELECT * FROM user where name=? and uid!=?").set(name).set(user.get("uid")).runList();
        //有记录且原名字与新名字不同
        if (ms.getSum()!= 0) {
            response.getWriter().print("<script>alert('用户名称已存在，请重新输入');window.location='userChange'</script>");
            return;
        }
        // --------------------------------------------修改----------------------------------------------------
        ms.setSql("UPDATE user SET name=?,password=?,phone=? WHERE uid=?");
        String[]top=(String[]) session.getAttribute("top");
        top=Tools.delString(top,"rank");
        for (String string : top) {
            ms.set(request.getParameter(string));
        }
        ms.set(Integer.parseInt(user.get("uid").toString()));
        if(ms.run()>0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("userMap",UserController.initializationUser(ms));
            response.setHeader("refresh", "0;URL=Logout");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='userChange'</script>");
        }
    }
    @RequestMapping("/userList")
    public String userList(HttpServletRequest request) {
        //----------------------------------------判断传值并写入session------------------------------------------------
        ms.setSql("SELECT * from user where status=1 order by uid asc limit ?,?");
        ms.runPagination(request, "/userList", 10);
        String [] top=Tools.delString(ms.getTop(),"status");
        top=Tools.delString(top,"password");
        request.setAttribute("top", top);
        //----------------------------------转发------------------------------------------------------------
        return "userList";
    }
    @RequestMapping("/userDel")
    public void userDel(HttpServletRequest request,HttpServletResponse response, String uid) throws IOException {
        int i=ms.setSql("update user set status=0 where uid=?").set(uid).run();
        if(i>0) {
            ServletContext application= request.getSession().getServletContext();
            application.setAttribute("userMap",UserController.initializationUser(ms));
            response.getWriter().print("<script>alert('已删除');window.location='userList'</script>");
        } else {
            response.getWriter().print("<script>alert('删除失败');window.location='userList'</script>");
        }
    }
    @RequestMapping("/userReset")
    public void userReset(HttpServletResponse response, String uid) throws IOException {
        int i=ms.setSql("update user set password=name where uid=?").set(uid).run();
        if(i>0) {
            response.getWriter().print("<script>alert('已重置密码为账号名');window.location='userList'</script>");
        } else {
            response.getWriter().print("<script>alert('重置密码失败');window.location='userList'</script>");
        }
    }
    @RequestMapping("/userChangeRank")
    public String userChangeRank(HttpServletRequest request,String uid){
        HttpSession session=request.getSession();
        ms.setSql("select uid,name,`rank` from user where uid=?").set(Integer.parseInt(uid));
        session.setAttribute("list",ms.runList());
        session.setAttribute("top",ms.getTop());
        session.setAttribute("uid",uid);
        return "userChangeRank";
    }
    @RequestMapping("/UserChangeRank")
    public void UserChangeRank(HttpServletRequest request, HttpServletResponse response,String rank) throws IOException {
        //用户管理员无法赋予超级管理员权限.
        HttpSession session=request.getSession();
        if(Integer.parseInt(rank)==1 &&Integer.parseInt(((LinkedHashMap<String, Object>) session.getAttribute("user")).get("rank").toString())==6){
            response.getWriter().print("<script>alert('你无权添加超级管理员权限');window.location='userList'</script>");
            return;
        }
        // --------------------------------------------修改权限----------------------------------------------------
        ms.setSql("UPDATE user SET `rank`=? WHERE uid=?").set(rank);
        ms.set(Integer.parseInt(session.getAttribute("uid").toString()));
        //调试方法
        System.out.println('\n'+ms.getSql());
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=userList");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='userList'</script>");
        }
    }
}
