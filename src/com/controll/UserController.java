package com.controll;

import com.aotmd.Tools;
import com.aotmd.Translate;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
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
    @RequestMapping("/main")
    public String main(){
        return "main";
    }
    @RequestMapping("/login")
    public String login(HttpServletRequest request){
        //设置翻译图
        request.getSession().setAttribute("translate", Translate.getTranslate());
        return "login";
    }
    @RequestMapping("/Login")
    public String login(HttpServletRequest request, HttpServletResponse response, String username, String password) throws IOException {
        ms.setSql("select * from user where username=? and password=?").set(username).set(password);
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
    @RequestMapping("reg")
    public String reg(HttpServletRequest request){
        ms.setSql("select * from user").runList();
        String []top=ms.getTop();
        top=Tools.delString(top,"id");
        System.out.println(Arrays.toString(top));
        request.getSession().setAttribute("top",top );
        return "reg";
    }
    @RequestMapping("/Reg")
    public void reg(HttpServletRequest request, HttpServletResponse response,String username) throws IOException {
        String []top= (String[]) request.getSession().getAttribute("top");
        ms.setSql("select * from user where username=?").set(username);
        if(ms.runList().size()>0) {
            response.getWriter().print("<script>alert('注册失败,名称重复');window.location='reg'</script>");
            return;
        }
        ms.setSql("insert into user value(0,?,?,?,?)");
        for (String value : top) {
            String s = request.getParameter(value);
            ms.set(s);
        }
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=login");
        } else {
            response.getWriter().print("<script>alert('注册失败');window.location='reg'</script>");
        }
    }
    @RequestMapping("/userChange")
    public String userChange(HttpServletRequest request){
        return "userChange";
    }
    @RequestMapping("/UserChange")
    public void userChange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(ms.run()>0) {
            response.setHeader("refresh", "0;URL=Logout");
        } else {
            response.getWriter().print("<script>alert('修改失败');window.location='userChange'</script>");
        }
    }
    @RequestMapping("/userList")
    public String userList(HttpServletRequest request, HttpServletResponse response, String t) throws IOException {
        return "userList";
    }
    @RequestMapping("/userDel")
    public void userDel(HttpServletRequest request, HttpServletResponse response,String tid,String sid) throws IOException {

    }
}
