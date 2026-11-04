<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Order placed | QuickCart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<header class="topbar"><a class="brand" href="${pageContext.request.contextPath}/products"><span class="brand-mark">Q</span> quick<span>cart</span></a></header>
<main class="container narrow"><section class="success-card"><div class="success-icon">✓</div><p class="eyebrow">ORDER CONFIRMED</p><h1>Thanks for shopping!</h1>
<p>Your demo order has been saved to the database.</p><div class="order-number">Order number <strong>#<c:out value="${orderId}"/></strong></div>
<a class="button button-dark" href="${pageContext.request.contextPath}/products">Continue shopping</a></section></main></body></html>
