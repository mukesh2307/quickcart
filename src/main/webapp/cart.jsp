<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.Map,java.math.BigDecimal,com.blinkit.model.CartItem" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
Map<Integer,CartItem> cart=(Map<Integer,CartItem>)session.getAttribute("cart");
BigDecimal total=BigDecimal.ZERO;
if (cart!=null) for (CartItem item:cart.values()) total=total.add(item.getLineTotal());
request.setAttribute("cartTotal",total);
%>
<!DOCTYPE html><html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Your cart | QuickCart</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css"></head><body>
<header class="topbar"><a class="brand" href="${pageContext.request.contextPath}/products"><span class="brand-mark">Q</span> quick<span>cart</span></a>
<div class="delivery"><strong>Your shopping cart</strong><small>Review your items before checkout</small></div>
<a class="cart-link" href="${pageContext.request.contextPath}/products">← Continue shopping</a></header>
<main class="container narrow"><div class="section-heading"><div><p class="eyebrow">YOUR BAG</p><h1>Shopping cart</h1></div></div>
<c:choose><c:when test="${empty sessionScope.cart}">
<div class="empty-state"><div class="empty-icon">🛒</div><h2>Your cart is empty</h2><p>Add a few everyday essentials to get started.</p>
<a class="button button-dark" href="${pageContext.request.contextPath}/products">Browse products</a></div>
</c:when><c:otherwise>
<div class="cart-layout"><div class="cart-items">
<c:forEach var="item" items="${sessionScope.cart.values()}">
<article class="cart-item"><img src="<c:out value='${item.imageUrl}'/>" alt="">
<div class="cart-item-main"><h3><c:out value="${item.name}"/></h3><p>₹<c:out value="${item.price}"/> each</p>
<div class="cart-actions">
<form class="quantity-form" method="post" action="${pageContext.request.contextPath}/cart">
<input type="hidden" name="action" value="update"><input type="hidden" name="productId" value="${item.productId}">
<label for="qty-${item.productId}">Qty</label><input id="qty-${item.productId}" type="number" min="0" max="99" name="quantity" value="${item.quantity}">
<button class="button button-small" type="submit">Update</button></form>
<form method="post" action="${pageContext.request.contextPath}/cart"><input type="hidden" name="action" value="remove"><input type="hidden" name="productId" value="${item.productId}">
<button class="text-button" type="submit">Remove</button></form></div>
</div><strong class="line-total">₹<c:out value="${item.lineTotal}"/></strong></article>
</c:forEach>
</div><aside class="summary-card"><p class="eyebrow">ORDER SUMMARY</p><h2>Cart total</h2>
<div class="summary-line"><span>Subtotal</span><strong>₹<c:out value="${cartTotal}"/></strong></div>
<div class="summary-line"><span>Delivery</span><strong>Free</strong></div><div class="summary-line summary-total"><span>Total</span><strong>₹<c:out value="${cartTotal}"/></strong></div>
<a class="button button-dark full-button" href="${pageContext.request.contextPath}/checkout">Proceed to checkout</a>
<form method="post" action="${pageContext.request.contextPath}/cart"><input type="hidden" name="action" value="clear"><button class="text-button clear-button" type="submit">Clear cart</button></form>
</aside></div>
</c:otherwise></c:choose></main></body></html>
