<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin login | QuickCart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body class="admin-login-page">
<header class="topbar">
  <a class="brand" href="${pageContext.request.contextPath}/login"><span class="brand-mark">Q</span> quick<span>cart</span></a>
  <a class="cart-link" href="${pageContext.request.contextPath}/login">Customer login</a>
</header>
<main class="admin-login-card">
  <p class="eyebrow">STAFF ACCESS</p><h1>Administrator login</h1>
  <p class="muted">Sign in to manage inventory and review orders.</p>
  <c:if test="${not empty error}"><div class="error"><c:out value="${error}"/></div></c:if>
  <form class="admin-login-form" method="post" action="${pageContext.request.contextPath}/admin/login">
    <label for="admin-email">Admin email</label>
    <input id="admin-email" type="email" name="email" autocomplete="username" value="<c:out value='${email}'/>" required>
    <label for="admin-password">Password</label>
    <input id="admin-password" type="password" name="password" autocomplete="current-password" required>
    <button class="button button-dark" type="submit">Sign in to admin</button>
  </form>
  <p class="admin-login-help">Admin accounts are created by the site owner; there is no public admin signup.</p>
</main>
</body></html>
