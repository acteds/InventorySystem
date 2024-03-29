﻿<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>库存管理系统</title>

<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>

<!--阿里图标库-->
<%--<link rel="stylesheet" type="text/css" href="https://at.alicdn.com/t/c/font_3449887_8rfnnvlr76i.css" />--%>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/font/iconfont.css" />

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

</head>
<body>
<%--@elvariable id="name" type="java.lang.String"--%>
<%--@elvariable id="user" type="java.util.Map"--%>
<div class="body_con">
    <div class="body_top">
        <div style="display: inline-block;">库存管理系统</div>
        <%--<div style="display: inline-block;font-size: 14px;">当前用户:&nbsp;<span style="color: #9de614;">${user.realname!=null?user.realname:user.t_realname}</span>&nbsp;权限:${user.rank}</div>--%>
        <div style="display: inline-block;font-size: 14px;color: #9de614;padding-left: 6px;">${user.name}</div>
    </div>
    <div class="body_left">
        <ul class="body_left_list">
            <li>
                <label>
                    <span>个人中心</span>
                    <i class="iconfont icon-xiangqing1"></i>
                    <a href="javascript:;"></a>
                </label>
                <ul>
                    <li>
                        <label>
                            <i class="iconfont icon-edit-user"></i>
                            <span>修改信息</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="userChange" target="iframe_a"></a>
                        </label>
                    </li>
                    <li>
                        <label>
                            <i class="iconfont icon-zhuxiaologout11"></i>
                            <span>注销</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="Logout"></a>
                        </label>
                    </li>
                    <li>
                        <label>
                            <i class="iconfont icon-shuaxin"></i>
                            <span>刷新</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="javaScript:document.getElementsByName('iframe_a')[0].contentWindow.location.reload();"></a>
                        </label>
                    </li>
                </ul>
            </li>
            <li>
                <label>
                    <span>子系统</span>
                    <i class="iconfont icon-xiangqing1"></i>
                    <a href="javascript:;"></a>
                </label>
                <ul><c:if test="${fn:contains([1,2,3,4,5,7],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-cangkuchaxunguanli"></i>
                            <span>库存总览</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="publicInventorySumList" target="iframe_a"></a>
                        </label>
                    </li></c:if>
                    <c:if test="${fn:contains([1,2],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-rukudan"></i>
                            <span>入库管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="inventoryAddList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${fn:contains([1,3],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-chukudan"></i>
                            <span>出库管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="inventorySubList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${fn:contains([1,4],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-lishidingdan"></i>
                            <span>入库审核</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="inventoryReviewAddList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${fn:contains([1,5],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-lishidingdan"></i>
                            <span>出库审核</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="inventoryReviewSubList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${fn:contains([1,6],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-yonghuguanli"></i>
                            <span>用户管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="userList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${fn:contains([1,7],user.rank)}">
                    <li>
                        <label>
                            <i class="iconfont icon-huopinxinxi"></i>
                            <span>类别管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="goodsList" target="iframe_a"></a>
                        </label>
                    </li></c:if><c:if test="${user.rank==1}">
                    <li>
                        <label>
                            <i class="iconfont icon-peizhi"></i>
                            <span>参数管理</span>
                            <i class="iconfont icon-xiangqing2"></i>
                            <a href="parametersMainList" target="iframe_a"></a>
                        </label>
                    </li></c:if>
                </ul>
            </li>
        </ul>
    </div>
    <div class="body_right"><c:if test="${fn:contains([1,2,3,4,5,7],user.rank)}">
        <iframe src="publicInventorySumList" width="100%" height="100%" name="iframe_a" style="border: 0"></iframe></c:if>
        <c:if test="${user.rank==6}">
        <iframe src="userList" width="100%" height="100%" name="iframe_a" style="border: 0"></iframe>
        </c:if>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/script.js"></script>
</body>
</html>
