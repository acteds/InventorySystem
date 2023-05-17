<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%--@elvariable id="top" type="java.lang.String"--%>
    <%--@elvariable id="username" type="java.lang.String"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>参数信息管理</title>
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
	<h2>参数信息管理</h2><%--@elvariable id="sum" type="int"--%>
	<table border="1" class="table table-striped table-bordered table-condensed table-hover">
		<tr><%--@elvariable id="bar" type="java.lang.String"--%>
			<td colspan="${fn:length(top)}" align="left">当前用户:&nbsp;<span
					class="fonts2">${user.name}</span></td>
			<td align="center" >${bar}</td>
		</tr>
		<tr class="fonts3"><%--@elvariable id="translate" type="java.util.Map"--%>
		<c:forEach var="temp" items="${top}">
			<td>${translate.get(temp)!=null?translate.get(temp):temp}</td>
		</c:forEach>
			<td>操作</td>
		</tr><%--@elvariable id="list" type="java.util.List"--%>
		<c:forEach var="temp" items="${list}">
		<tr><%--@elvariable id="aidmap" type="java.util.Map"--%>
			<c:forEach var="key" items="${top}">
				<td>${temp.get(key)}</td>
			</c:forEach>
			<td align="center">
				<a onClick="return deleteDemo(`${temp.get('name')}`,`${temp.get('ParametersSubCount')}`)" href="ParametersMainDel?${top[0]}=${temp.get(top[0])}" class="fonts4">删除</a>|
				<a href="parametersMainChange?${top[0]}=${temp.get(top[0])}">修改</a>|
				<a href="parametersSubList?${top[0]}=${temp.get(top[0])}&name=${temp.get('name')}">查看参数详情</a>
			</td>
		</tr>
		</c:forEach>
		<tr><td colspan="${fn:length(top)+1}" align="center"><a class="fonts1" href="parametersMainInsert">添加参数名称</a></td></tr>
	</table>
</div>
</center>
<script type="text/javascript">
    //删除提示
    function deleteDemo(name,size) {
		if (size > 0) {
			alert('该参数名称下存在参数,请先删除参数');
			return false;
		}
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