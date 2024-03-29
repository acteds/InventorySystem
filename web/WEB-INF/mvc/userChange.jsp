<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<%--@elvariable id="translate" type="java.util.Map"--%>
<%--@elvariable id="user" type="java.util.Map"--%>
<%--@elvariable id="cidmap" type="java.util.Map"--%>
<head>
    <title>${user.name}用户信息修改</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
	<style type="text/css">.fonts2 {background-color: #eeebe5;border: 1px black solid;}</style>
</head>
<body>
<center>
<div class="message">
<form name="form" action="UserChange" method="post" onsubmit="return NotNull(this);">
	<table width="317"><%--@elvariable id="top" type="java.lang.String"--%>
		<tr><td colspan="2" class="title">${user.name}用户信息修改</td></tr>
		<tr><td width="100" align="right"></td><td width="217"></td></tr>
<c:forEach var="st" items="${top}">
	<c:if test="${st=='password'}" var="if1" scope="page">
		<tr><td align="right">${translate.get(st)}:</td><td><input name="${st}0" type="password"></td></tr>
		<tr><td align="right">新密码:</td><td><input name="${st}" type="password"></td></tr>
		<tr><td align="right">确认密码:</td><td><input name="${st}2" type="password"></td></tr>
	</c:if>
	<c:if test="${st=='rank'}" var="if2" scope="page"><%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
		<tr><td align="right">${translate.get(st)}:</td>
			<td><input name="${st}" type="text" value="${parameters.get("2").get(user.get(st))}" readonly="readonly" class="fonts2"></td>
		</tr>
	</c:if>
	<c:if test="${not if1 and not if2}">
		<tr><td align="right">${translate.get(st)}:</td>
			<td><input name="${st}" type="text" value="${user.get(st)}"></td>
		</tr>
	</c:if>
</c:forEach>
		<tr><td colspan="2" align="center" style="padding-top: 10px">
				<input name="start" type="submit" value="修改" class="botton">
				<input name="reset" type="reset" value="重置" class="botton">
				<a href="JavaScript:window.history.go(-1);"><input name="button" type="button" value="返回" class="botton"></a>
			</td>
		</tr>
	</table>
</form>
</div>
</center>
<script type="text/javascript">
    //判断是否为空(动态)
    /**
	 * @return {boolean}
	 */
	function NotNull() {
        var flag = 0, sum = "";
        <c:forEach var="st" items="${top}">
        <c:if test="${st=='password'||st=='t_password'}" var="if1" scope="page">
        if (form.${st}${0}.value == "") {
            sum += "${translate.get(st)}不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        if (form.${st}.value == "") {
            sum += "新密码不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        if (form.${st}${2}.value == "") {
            sum += "确认密码不能为空\n";
            form.${st}${2}.focus();
            flag = 1;
        }
        if (form.${st}.value != form.${st}${2}.value) {
            sum += "密码不一致\n";
            form.${st}.focus();
            flag = 1;
        }
        </c:if>
        <c:if test="${not if1}">
        if (form.${st}.value == "") {
            sum += "${translate.get(st)}不能为空\n";
            form.${st}.focus();
            flag = 1;
        }
        </c:if>
        </c:forEach>
        if (flag == 1) {
            window.alert(sum);
            return false;
        } else return true;
    }
</script>
</body>
</html>