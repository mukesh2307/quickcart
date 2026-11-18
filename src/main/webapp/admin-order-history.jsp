<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Order history | QuickCart Admin</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body>
<%@ include file="/WEB-INF/jspf/admin-nav.jspf" %>
<main class="container admin-container">
  <div class="admin-page-heading"><div><p class="eyebrow">PAST SALES</p><h1>Order history</h1></div></div>
  <c:if test="${not empty selectedOrder}">
    <section class="admin-panel admin-order-detail">
      <div class="admin-section-heading"><div><p class="eyebrow">ORDER #<c:out value="${selectedOrder.id}"/></p><h2><c:out value="${selectedOrder.customerName}"/></h2></div>
        <span class="admin-status"><c:out value="${selectedOrder.status}"/></span></div>
      <p><strong>Placed:</strong> <c:out value="${selectedOrder.createdAt}"/></p>
      <p><strong>Delivery address:</strong> <c:out value="${selectedOrder.deliveryAddress}"/></p>
      <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Product</th><th>Quantity</th><th>Unit price</th><th>Line total</th></tr></thead><tbody>
        <c:forEach var="line" items="${orderLines}"><tr>
          <td><c:out value="${line.productName}"/></td><td><c:out value="${line.quantity}"/></td>
          <td>₹<c:out value="${line.unitPrice}"/></td><td>₹<c:out value="${line.lineTotal}"/></td>
        </tr></c:forEach>
      </tbody><tfoot><tr><th colspan="3">Order total</th><th>₹<c:out value="${selectedOrder.totalAmount}"/></th></tr></tfoot></table></div>
    </section>
  </c:if>
  <section class="admin-panel">
    <div class="admin-section-heading"><div><p class="eyebrow">ALL ORDERS</p><h2>Order history</h2></div></div>
    <c:choose><c:when test="${empty orders}"><p class="muted">No order history yet.</p></c:when><c:otherwise>
      <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Order</th><th>Customer</th><th>Date</th><th>Total</th><th>Status</th><th>Details</th></tr></thead><tbody>
      <c:forEach var="order" items="${orders}"><tr>
        <td>#<c:out value="${order.id}"/></td><td><c:out value="${order.customerName}"/></td>
        <td><c:out value="${order.createdAt}"/></td><td>₹<c:out value="${order.totalAmount}"/></td>
        <td><span class="admin-status"><c:out value="${order.status}"/></span></td>
        <td><a class="admin-text-link" href="${pageContext.request.contextPath}/admin/order-history?orderId=${order.id}">View items</a></td>
      </tr></c:forEach></tbody></table></div>
    </c:otherwise></c:choose>
  </section>
</main>
</body></html>
