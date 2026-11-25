<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String mode = (String) request.getAttribute("mode");
    boolean isRegister = "register".equals(mode);
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    request.setAttribute("authError", error);
    request.setAttribute("authSuccess", success);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= isRegister ? "Create account" : "Login" %> | QuickCart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-page">
    <header class="topbar auth-topbar">
        <a href="<%= request.getContextPath() %>/login" class="brand" aria-label="QuickCart home">
            <span class="brand-mark" aria-hidden="true">Q</span> quick<span>cart</span>
        </a>
        <span class="auth-promise">Everyday essentials, made simple.</span>
        <% if (session.getAttribute("loggedInUser") != null) { %>
            <form method="post" action="<%= request.getContextPath() %>/logout" class="customer-logout-form">
                <button class="cart-link customer-logout" type="submit">Log out</button>
            </form>
        <% } %>
    </header>

    <main class="auth-main">
        <section class="auth-card" aria-labelledby="auth-title">
            <header class="auth-card-header">
                <p class="eyebrow"><%= isRegister ? "JOIN QUICKCART" : "WELCOME BACK" %></p>
                <h1 id="auth-title"><%= isRegister ? "Create your account" : "Welcome back" %></h1>
                <p><%= isRegister ? "Set up your account to browse groceries and place orders." : "Sign in to access your cart and continue shopping." %></p>
                <nav class="auth-mode-switcher" aria-label="Account options">
                    <a href="<%= request.getContextPath() %>/login"
                       class="<%= !isRegister ? "is-active" : "" %>" <%= !isRegister ? "aria-current=\"page\"" : "" %>>Login</a>
                    <a href="<%= request.getContextPath() %>/register"
                       class="<%= isRegister ? "is-active" : "" %>" <%= isRegister ? "aria-current=\"page\"" : "" %>>Create account</a>
                </nav>
            </header>

            <div class="auth-card-body">
                <c:if test="${not empty authError}">
                    <div class="error" role="alert"><c:out value="${authError}"/></div>
                </c:if>
                <c:if test="${not empty authSuccess}">
                    <div class="notice" role="status"><c:out value="${authSuccess}"/></div>
                </c:if>

                <form class="auth-form" method="post" action="<%= request.getContextPath() %><%= isRegister ? "/register" : "/login" %>">
                    <% if (isRegister) { %>
                        <label for="fullName">Full name</label>
                        <input id="fullName" type="text" name="fullName" autocomplete="name" placeholder="e.g., Aarav Sharma" required>
                    <% } %>

                    <label for="email">Email address</label>
                    <input id="email" type="email" name="email" autocomplete="email" placeholder="you@example.com" required>

                    <% if (isRegister) { %>
                        <label for="phone">Mobile number</label>
                        <input id="phone" type="tel" name="phone" autocomplete="tel" placeholder="9876543210" pattern="[0-9]{10}" required>

                        <label for="deliveryAddress">Default delivery address</label>
                        <textarea id="deliveryAddress" name="deliveryAddress" rows="2" autocomplete="street-address" placeholder="Flat / House No., Street, City" required></textarea>
                    <% } %>

                    <label for="password">Password</label>
                    <input id="password" type="password" name="password" autocomplete="<%= isRegister ? "new-password" : "current-password" %>" placeholder="Enter your password" minlength="6" required>

                    <% if (isRegister) { %>
                        <label for="confirmPassword">Confirm password</label>
                        <input id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" placeholder="Re-enter your password" minlength="6" required>
                    <% } %>

                    <button type="submit" class="button button-primary">
                        <%= isRegister ? "Create account" : "Login to continue" %>
                    </button>
                </form>
                <a class="button button-outline auth-admin-link" href="<%= request.getContextPath() %>/admin/login">Admin login</a>
            </div>
        </section>
    </main>

    <footer class="footer">QuickCart · Everyday essentials, made simple.</footer>
</body>
</html>
