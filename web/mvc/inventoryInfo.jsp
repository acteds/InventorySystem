<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%--@elvariable id="top" type="java.lang.String"--%>
    <%--@elvariable id="username" type="java.lang.String"--%>
	<%--@elvariable id="gname" type="java.lang.String"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${goodsName}详细信息</title>
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
	<h2>${goodsName}详细信息</h2><%--@elvariable id="sum" type="int"--%>
	<table border="1" class="table table-striped table-bordered table-condensed table-hover">
		<tr><%--@elvariable id="bar" type="java.lang.String"--%>
			<td colspan="${fn:length(top)-1}" align="left">当前用户:&nbsp;<span
					class="fonts2">${user.name}</span>&nbsp;&nbsp;&nbsp;&nbsp;<a href="publicInventorySumList">返回</a></td>
			<td align="center" >${bar}</td>
		</tr>
		<tr class="fonts3"><%--@elvariable id="translate" type="java.util.Map"--%>
		<c:forEach var="temp" items="${top}">
			<c:if test="${temp=='uid'}" var="if1" scope="page">
				<td>入库员</td>
			</c:if>
			<c:if test="${not if1}">
				<td>${translate.get(temp)!=null?translate.get(temp):temp}</td>
			</c:if>
		</c:forEach>
		</tr><%--@elvariable id="list" type="java.util.List"--%>
		<c:forEach var="temp" items="${list}">
		<tr><%--@elvariable id="userMap" type="java.util.LinkedHashMap"--%>
			<c:forEach var="key" items="${top}">
				<c:if test="${key=='uid'}" var="if1" scope="page">
					<td>${userMap.get(temp.get(key).toString())}</td>
				</c:if>
				<c:if test="${not if1}">
					<td>${temp.get(key)}</td>
				</c:if>
			</c:forEach>
		</tr>
		</c:forEach>
		<tr><td colspan="${fn:length(top)}" align="center">&nbsp;</td></tr>
	</table>
</div>
</center>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>