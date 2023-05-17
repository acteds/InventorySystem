<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><%--@elvariable id="top" type="java.lang.String"--%>
<head><%--@elvariable id="translate" type="java.util.Map"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>修改键值</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
</head>
<body>
<center>
<div class="message">
<form name="form1" action="ParametersSubChange" method="post" onsubmit="return NotNull();">
    <table width="317">
        <tr><td colspan="2" class="title">修改键值</td></tr>
        <tr><td width="100" align="right"></td><td width="217"></td>
        </tr><%--@elvariable id="list" type="java.util.LinkedHashMap"--%>
        <c:forEach var="st" items="${top}">
            <c:if test="${st== 'explanation'}" var="if1" scope="page">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><textarea style="width: 166px;" name="${st}">${list.get(st)}</textarea></td>
                </tr>
            </c:if>
            <c:if test="${st=='psid'}" var="if2" scope="page">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><input name="${st}" type="text" style="width: 167px;" value="${list.get(st)}" disabled="disabled"></td>
                </tr>
            </c:if>
            <c:if test="${st=='pmid'}" var="if3" scope="page">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><input name="${st}" type="text" style="width: 167px;" value="${list.get(st)}" disabled="disabled"></td>
                </tr>
            </c:if>
            <c:if test="${not if1 and not if2 and not if3}">
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><input name="${st}" type="text" style="width: 167px;" value="${list.get(st)}"></td>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <td colspan="2" align="center" style="padding-top: 10px">
                <input name="start" type="submit" value="修改" class="botton">
                <input name="reset" type="reset" value="重置" class="botton">
                <a href="parametersSubList"><input name="button" type="button" value="返回" class="botton"></a>
            </td>
        </tr>
    </table>
</form>
</div>
</center>
<script type="text/javascript">
    /**判断是否为空(动态)
     * @return {boolean}
     */
    function NotNull() {
        var flag = 0, sum = "";
        <c:forEach var="st" items="${top}">
        if (form1.${st}.value == "") {
            sum += "${translate.get(st)}不能为空\n";
            form1.${st}.focus();
            flag = 1;
        }
        </c:forEach>
        if (flag == 1) {
            window.alert(sum);
            return false;
        } else return true;
    }
</script>
</body>
</html>