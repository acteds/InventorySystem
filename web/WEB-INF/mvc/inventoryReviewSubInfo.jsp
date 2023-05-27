<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><%--@elvariable id="top" type="java.lang.String"--%>
<head><%--@elvariable id="translate" type="java.util.Map"--%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>审核结果</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media2.css">
    <style type="text/css">
        .fonts2 {background-color: #eeebe5;border: 1px black solid;}
        .underline-border{display: inherit;border: 0px;border-bottom: 1px black solid;background-color:#f9f6f0}
    </style>
</head>
<body>
<center>
    <div class="message">
        <form name="form1" action="InventoryReviewSubInfo" method="post" onsubmit="return deleteDemo(${inventorySub.get('review').toString()});">
            <table width="317">
                <tr><td colspan="2" class="title">审核结果</td></tr>
                <tr><td width="100" align="right"></td><td width="217"></td>
                </tr><%--@elvariable id="list" type="java.util.LinkedHashMap"--%>
                <c:forEach var="st" items="${top}">
                    <c:if test="${st== 'explanation'}" var="if1" scope="page">
                        <tr>
                            <td align="right">入库说明:</td>
                            <td><textarea style="width: 166px;" readonly="readonly" class="fonts2">${list.get(st)}</textarea></td>
                        </tr>
                    </c:if>
                    <c:if test="${st=='gid'}" var="if2" scope="page">
                        <tr><%--@elvariable id="goodsMap" type="java.util.Map"--%>
                            <td align="right">货品名称:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${goodsMap.get(list.get(st).toString())}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                    <c:if test="${st=='uid'}" var="if3" scope="page">
                        <tr><%--@elvariable id="userMap" type="java.util.Map"--%>
                            <td align="right">入库员:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${userMap.get(list.get(st).toString())}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                    <c:if test="${st=='quantity'}" var="quantityif" scope="page">
                        <tr>
                            <td align="right">现有${translate.get(st)!=null?translate.get(st):st}:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${list.get(st)}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                    <c:if test="${st=='createTime'}" var="if4" scope="page">
                        <tr>
                            <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;"
                                       value="<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${list.get(st)}"/>" readonly="readonly">
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${(not if1 and not if2 and not if3 and not if4 and not quantityif)}">
                        <tr>
                            <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${list.get(st)}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                </c:forEach>
                <tr><td style=" border-bottom: 1px #182026 solid;" colspan="2"></td></tr>
                <%--@elvariable id="top2" type="java.lang.String"--%>
                <c:forEach var="st" items="${top2}">
                    <%--@elvariable id="inventorySub" type="java.util.Map"--%>
                    <c:if test="${st=='uid'}" var="if5" scope="page">
                        <tr><%--@elvariable id="userMap" type="java.util.Map"--%>
                        <tr>
                            <td align="right">出库员:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${userMap.get(inventorySub.get(st).toString())}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                    <c:if test="${st=='createTime'}" var="if6" scope="page">
                        <tr>
                            <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;"
                                       value="<fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${inventorySub.get(st)}"/>" readonly="readonly">
                            </td>
                        </tr>
                    </c:if>
                    <c:if test="${st== 'explanation'}" var="if7" scope="page">
                        <tr>
                            <td align="right">出库说明:</td>
                            <td><textarea style="width: 166px;" readonly="readonly" class="fonts2">${inventorySub.get(st)}</textarea></td>
                        </tr>
                    </c:if>
                    <c:if test="${st== 'quantity'}" var="if8" scope="page">
                        <tr><c:set scope="page" var="quantity" value="quantity"/>
                            <td align="right">出库数量:</td>
                            <td><input type="text" class="underline-border" readonly="readonly" style="width: 167px;" value="${inventorySub.get("quantity")}"></td>
                        </tr>
                    </c:if>
                    <%--@elvariable id="parameters" type="java.util.LinkedHashMap"--%>
                    <c:if test="${st== 'review'}" var="if9" scope="page">
                        <tr><td style=" border-bottom: 1px #182026 solid;" colspan="2"></td></tr>
                        <tr>
                            <td align="right">审核状态:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;"
                                       value="${parameters.get("3").get(inventorySub.get(st).toString())}" readonly="readonly">
                            </td>
                        </tr>
                        <%--@elvariable id="inventory_review" type="java.util.Map"--%>
                        <tr><%--@elvariable id="userMap" type="java.util.Map"--%>
                            <td align="right">审核员:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;"
                                       value="${userMap.get(inventory_review.get("uid").toString())}" readonly="readonly">
                            </td>
                        </tr>
                        <tr>
                            <td align="right">审核说明:</td>
                            <td><textarea style="width: 166px;" readonly="readonly" class="fonts2">${inventory_review.get("explanation")}</textarea></td>
                        </tr>
                    </c:if>
                    <c:if test="${not if5 and not if6 and not if7 and not if8 and not if9}">
                        <tr>
                            <td align="right">${translate.get(st)!=null?translate.get(st):st}:</td>
                            <td><input type="text" class="underline-border" style="width: 167px;" value="${inventorySub.get(st)}" readonly="readonly"></td>
                        </tr>
                    </c:if>
                </c:forEach>
                <tr>
                    <td colspan="2" align="center" style="padding-top: 10px">
                        <input name="start" type="submit" value="确认消息" class="botton">
                        <input name="reset" type="reset" value="重置" class="botton">
                        <a href="inventorySubList"><input name="button" type="button" value="返回" class="botton"></a>
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
        if (form1.review.value == "") {
            sum += "${translate.get("review")}不能为空\n";
            form1.review.focus();
            flag = 1;
        }
        if (form1.explanation.value == "") {
            sum += "${translate.get("explanation")}不能为空\n";
            form1.explanation.focus();
            flag = 1;
        }
        if (flag == 1) {
            window.alert(sum);
            return false;
        } else return true;
    }
    //确认提示
    function deleteDemo(review) {
        if (review<10){
            //没通过,不会隐藏.
            return true;
        }
        if (window.confirm('确认消息后此清单会隐藏,是否确认?')) {//alert("确定");
            return true;
        } else {//alert("取消");
            return false;
        }
    }
</script>
</body>
</html>