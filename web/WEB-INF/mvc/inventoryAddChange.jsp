<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><%--@elvariable id="top" type="java.lang.String"--%>
<head><%--@elvariable id="translate" type="java.util.Map"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>修改货品入库清单</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
    <style type="text/css">.fonts2 {background-color: #eeebe5;border: 1px black solid;}</style>
</head>
<body>
<center>
<div class="message">
<form name="form1" action="InventoryAddChange" method="post" onsubmit="return NotNull();">
    <table width="317">
        <tr><td colspan="2" class="title">修改货品入库清单</td></tr>
        <tr><td width="100" align="right"></td><td width="217"></td>
        </tr><%--@elvariable id="list" type="java.util.LinkedHashMap"--%>
        <c:forEach var="st" items="${top}">
            <c:if test="${st== 'explanation'}" var="if1" scope="page">
                <tr>
                    <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                    <td><textarea style="width: 166px;" name="${st}">${list.get(st)}</textarea></td>
                </tr>
            </c:if>
            <c:if test="${st=='gid'}" var="if2" scope="page">
                <%--@elvariable id="translate" type="java.util.Map"--%>
                <%--@elvariable id="goodsMap" type="java.util.Map"--%>
                <td align="right">货品名称:</td>
                <td>
                    <select name="${st}" id="sort" size="1">
                        <c:forEach var="entry" items="${goodsMap}">
                            <option value='${entry.key}'<c:if test="${list.get(st).toString() == entry.key}"> selected = "selected"</c:if>>
                                    ${entry.value}</option>
                        </c:forEach>
                    </select>
                </td>
            </c:if>
            <c:if test="${st=='iid'}" var="if3" scope="page"><%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
                <tr><td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                    <td><input name="${st}" type="text" value="${list.get(st)}" readonly="readonly" class="fonts2"></td>
                </tr>
            </c:if>
            <c:if test="${st=='createTime'}" var="if4" scope="page"><%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
                <tr><td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                    <td><input name="${st}" type="text" value="<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${list.get(st)}"/>" readonly="readonly" class="fonts2"></td>
                </tr>
            </c:if>
            <c:if test="${st=='quantity'}" var="if5" scope="page">
                <%--@elvariable id="translate" type="java.util.Map"--%>
                <%--@elvariable id="goodsMap" type="java.util.Map"--%>
                <tr>
                    <td align="right">${translate.get(st)}:</td>
                    <td><input name="${st}" type="number" min="0" style="width: 167px;" value="${list.get(st)}"></td>
                </tr>
            </c:if>

            <c:if test="${not if1 and not if2 and not if3 and not if4 and not if5}">
                <tr>
                    <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                    <td><input name="${st}" type="text" style="width: 167px;" value="${list.get(st)}"></td>
                </tr>
            </c:if>
        </c:forEach>
        <tr>
            <td colspan="2" align="center" style="padding-top: 10px">
                <input name="start" type="submit" value="修改" class="botton">
                <input name="reset" type="reset" value="重置" class="botton">
                <a href="inventoryAddList"><input name="button" type="button" value="返回" class="botton"></a>
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