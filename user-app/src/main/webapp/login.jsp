<%@ page import="java.util.Enumeration" %>
<head>
    <jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
    <title>登录</title>
</head>
<body>
<div class="container">
    <h1>用户登录</h1>
    <h6>已注册成功</h6>
    <form action="/login" method="post">
        <table>
            <tr>
                <td>用户：<input name="name" type="text"></td>
            </tr>
            <tr>
                <td>密码：<input name="password" type="text"></td>
            </tr>
            <tr>
                <td>邮箱：<input name="email" type="text"></td>
            </tr>
            <tr>
                <td>手机号：<input name="phoneNumber" type="text"></td>
            </tr>
            <tr>
                <td><input name="submit" type="submit" value="提交"></td>
            </tr>
        </table>
    </form>
</div>
</body>