package com.controll;

import com.aotmd.Tools;
import com.dao.MySql;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

/**
 * 库存查询
 * @author aotmd
 * @version 1.0
 * @date 2023/3/23 12:32
 */
@Controller
public class InventoryController {
    private MySql ms;

    public InventoryController(MySql ms) {
        this.ms = ms;
    }
    @RequestMapping("/publicInventorySumList")
    public String publicInventorySumList(HttpServletRequest request) {
        ms.setSql("SELECT gid,sum(quantity) as quantity FROM inventory " +
                "where review>=10 and status=0 GROUP BY gid order by gid asc limit ?,?");
        ms.runPagination(request, "/publicInventorySumList", 10);
        String []top=ms.getTop();
        request.setAttribute("top", top);
        return "publicInventorySumList";
    }
    @RequestMapping("/publicInventoryInfo")
    public String publicInventoryInfo(HttpServletRequest request,String gid) {
        LinkedHashMap<String, String> goods=(LinkedHashMap<String, String>) request.getSession().getServletContext().getAttribute("goodsMap");
        ms.setSql("SELECT * FROM inventory where gid=? and review>=10 and status=0 order by createTime desc limit ?,?");
        ms.set(Integer.parseInt(gid));
        ms.runPagination(request, "/publicInventoryInfo", 10);
        String []top=ms.getTop();
        top=Tools.delString(top,"review");
        top=Tools.delString(top,"gid");
        top=Tools.delString(top,"status");
        top=Tools.delString(top,"explanation");
        String goodsName=goods.get(gid);
        request.setAttribute("top", top);
        request.setAttribute("goodsName", goodsName);
        return "publicInventoryInfo";
    }
}
