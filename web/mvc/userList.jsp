<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%--@elvariable id="top" type="java.lang.String"--%>
    <%--@elvariable id="username" type="java.lang.String"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${username}账号管理</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/List.css" rel="stylesheet">
    <style type="text/css">
        .c1 {
            width: 960px;
        }

        .yc {
            color: #6E6E6E;
            background-color: #6E6E6E;
            text-decoration: none;
        }

        .yc:hover, .yc:active {
            color: inherit;
            background-color: inherit;
            text-decoration: none;
        }
    </style>
</head>
<body>
<center>
<div class="c1"><%--@elvariable id="user" type="java.util.Map"--%>
	<h2>账号管理</h2><%--@elvariable id="sum" type="int"--%>
	<table border="1" class="table table-striped table-bordered table-condensed table-hover">
		<tr><%--@elvariable id="bar" type="java.lang.String"--%>
			<td colspan="${fn:length(top)}" align="left">当前用户:&nbsp;<span
					class="fonts2">${user.name}</span></td>
			<td align="center" >${bar}</td>
		</tr>
		<tr class="fonts3"><%--@elvariable id="translate" type="java.util.Map"--%>
		<c:forEach var="temp" items="${top}">
			<td>${translate.get(temp)}</td>
		</c:forEach>
			<td>操作</td>
		</tr><%--@elvariable id="list" type="java.util.List"--%>
		<c:forEach var="temp" items="${list}">
		<tr><%--@elvariable id="aidmap" type="java.util.Map"--%>
			<c:forEach var="key" items="${top}">
				<c:if test="${key=='password'}" var="if1" scope="page">
					<td><span class="yc">${temp.get(key)}</span></td>
				</c:if>
				<c:if test="${key=='rank'}" var="if2" scope="page"><%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
					<td>${parameters.get("2").get(temp.get(key))}</td>
				</c:if>
				<c:if test="${not if1 and not if2}">
					<td>${temp.get(key)}</td>
				</c:if>
			</c:forEach>
			<td align="center"><%--限制用户管理员修改超管账号--%><%--@elvariable id="Integer" type="java.lang.Integer"--%>
				<c:if test="${user.rank==1 or (temp.rank!=1 and user.rank==6)}" var="if1" scope="page">
					<a onClick="return deleteDemo(`${temp.get('name')}`)" href="userDel?${top[0]}=${temp.get(top[0])}" class="fonts4">删除</a>|
					<a href="userReset?${top[0]}=${temp.get(top[0])}">重置密码</a>|
					<a href="userChangeRank?${top[0]}=${temp.get(top[0])}">修改权限</a>
				</c:if>
				<c:if test="${not if1}">
					无法变更
				</c:if>
			</td>
		</tr>
		</c:forEach>
		<tr><td colspan="${fn:length(top)+1}" align="center"><a class="fonts1" href="userInsert">添加账号</a></td></tr>
	</table>
</div>
</center>
<script type="text/javascript">
    //删除提示
    function deleteDemo(name) {
        if (window.confirm('你确定要删除' + name + '吗？')) {//alert("确定");
            return true;
        } else {//alert("取消");
            return false;
        }
    }
</script>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>