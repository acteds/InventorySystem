<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd" >
<html>
<head>
    <title>库存系统登录</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/media.css">
    <style>
        h1{
            margin-top: 5%;
            text-align: center;
            color: snow;
        }
    </style>
</head>
<body>

<center>
    <div class="message">
        <form name="form1" action="Login" method="post" onsubmit="return notNull(this);">
            <table width="317">
                <tr><td colspan="2" class="title">库存系统登录</td></tr>
                <tr>
                    <td width="100" align="right">用户名:</td>
                    <td width="217"><input name="username" type="text" required></td>
                </tr>
                <tr>
                    <td align="right">密&nbsp;&nbsp;&nbsp;&nbsp;码:</td>
                    <td><input name="password" type="password" required></td>
                </tr>
                <tr>
                    <td align="right"><img class="verifyCode" onclick="changeCode()" src="getVerifyCode" style="width:100%"></td>
                    <td><input class="verifyInput" name="verifyInput" placeholder="请输入验证码" required  autocomplete="off"></td>
                </tr>
                <tr>
                    <td colspan="2" align="center" style="padding-top: 10px"><input name="start" type="submit" value="登录" class="botton">
                        <input name="reset" type="reset" value="重置" class="botton"></td>
                </tr>
            </table>
        </form>
    </div>
</center>
<script language="javascript" type="text/javascript">
    //写一个函数判断用户是不是提交空的
    function notNull() {
        var flag = 0, sum = "";
        if (form1.username.value == "") {
            sum += "用户名不能为空\n";
            form1.username.focus();
            flag = 1;
        }
        if (form1.password.value == "") {
            sum += "密码不能为空\n";
            form1.password.focus();
            flag = 1;
        }
        if (form1.verifyInput.value == "") {
            sum += "验证码不能为空\n";
            form1.verifyInput.focus();
            flag = 1;
        }
        if (flag == 1) {
            window.alert(sum);
            return false;
        } else {
            initialization();
            return true;
        }
    }

    function initialization() {
        let count=0;
        let text=document.getElementsByTagName("h1")[0];
        setInterval(()=>{
            if (count>5){count=0}
            let string="初始化中";
            for (let i = 0; i < count; i++) {
                string+='.';
            }
            text.innerHTML=string;
            count++
        },500)
    }
    //判断是否是顶层容器
    window.onload=function(){
        if(self!==top){
            top.location.href="login";
        }
    };
    function changeCode() {
        const src = "${pageContext.request.contextPath}/getVerifyCode?" + new Date().getTime(); //加时间戳，防止浏览器利用缓存
        document.getElementsByClassName('verifyCode')[0].setAttribute('src',src);
    }
</script>
<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
</body>
</html>