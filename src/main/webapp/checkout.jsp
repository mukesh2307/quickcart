<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Map,java.math.BigDecimal,com.blinkit.model.CartItem" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
Map<Integer,CartItem> cart=(Map<Integer,CartItem>)session.getAttribute("cart");
BigDecimal total=BigDecimal.ZERO;
if(cart!=null) for(CartItem item:cart.values()) total=total.add(item.getLineTotal());
request.setAttribute("checkoutTotal",total);
%>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Checkout | QuickCart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<header class="topbar"><a class="brand" href="${pageContext.request.contextPath}/products"><span class="brand-mark">Q</span> quick<span>cart</span></a>
<div class="delivery"><strong>Almost there</strong><small>Complete your order details</small></div><a class="cart-link" href="${pageContext.request.contextPath}/cart">← Back to cart</a></header>
<main class="container narrow"><p class="eyebrow">LAST STEP</p><h1>Checkout</h1>
<c:if test="${not empty error}"><div class="error"><c:out value="${error}"/></div></c:if>
<div class="checkout-grid"><form class="checkout-form" method="post" action="${pageContext.request.contextPath}/checkout">
<label for="customerName">Your name</label><input id="customerName" name="customerName" maxlength="120" required placeholder="Enter your full name">
<label for="address">Delivery address</label><textarea id="address" name="address" maxlength="500" rows="4" required placeholder="House number, street, area, city, PIN code"></textarea>
<div class="payment-note"><strong>Demo checkout</strong><p>No online payment is collected in this college project.</p></div>
<button class="button button-dark full-button" type="submit">Place order · ₹<c:out value="${checkoutTotal}"/></button>
</form><aside class="summary-card"><p class="eyebrow">ORDER SUMMARY</p>
<c:forEach var="item" items="${sessionScope.cart.values()}"><div class="summary-line"><span><c:out value="${item.name}"/> × ${item.quantity}</span><strong>₹<c:out value="${item.lineTotal}"/></strong></div></c:forEach>
<div class="summary-line summary-total"><span>Total</span><strong>₹<c:out value="${checkoutTotal}"/></strong></div></aside></div>
</main></body></html>
