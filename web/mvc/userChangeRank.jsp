<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<%--@elvariable id="translate" type="java.util.Map"--%>
<%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
<%--@elvariable id="list" type="java.util.LinkedHashMap"--%>
<head>
    <title>${list.get(0).get('name')}用户权限修改</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
	<style type="text/css">.fonts2 {background-color: #EBEBE4;}</style>
</head>
<body>
<center>
<div class="message">
<form name="form" action="UserChangeRank" method="post" onsubmit="return NotNull(this);">
	<table width="317"><%--@elvariable id="top" type="java.lang.String"--%>
		<tr><td colspan="2" class="title">${list.get(0).get('name')}用户权限修改</td></tr>
		<tr><td width="100" align="right"></td><td width="217"></td></tr>
<c:forEach var="st" items="${top}">
	<c:if test="${st=='rank'}" var="if1" scope="page">
		<%--@elvariable id="translate" type="java.util.Map"--%>
		<td align="right">${translate.get(st)}:</td>
		<td>
			<select name="${st}" id="sort" size="1">
				<c:forEach var="entry" items="${parameters.get('2')}">
					<option value='${entry.key}' <c:if test="${list.get(0).get(st).toString() == entry.key}"> selected = "selected"</c:if>>
							${entry.value}</option>
				</c:forEach>
			</select>
		</td>
	</c:if>
	<c:if test="${not if1}">
		<tr><td align="right">${translate.get(st)}:</td>
			<td><input name="${st}" type="text" value="${list.get(0).get(st)}" disabled="disabled"></td>
		</tr>
	</c:if>
</c:forEach>
		<tr><td colspan="2" align="center" style="padding-top: 10px">
				<input name="start" type="submit" value="修改" class="botton">
				<input name="reset" type="reset" value="重置" class="botton">
				<a href="userList"><input name="button" type="button" value="返回" class="botton"></a>
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