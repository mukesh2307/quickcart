<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Orders | QuickCart Admin</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body class="admin-page">
<%@ include file="/WEB-INF/jspf/admin-nav.jspf" %>
<main class="container admin-container">
  <div class="admin-page-heading"><div><p class="eyebrow">FULFILMENT</p><h1>Orders</h1></div>
    <a class="button button-primary" href="${pageContext.request.contextPath}/admin/order-history">Full order history</a></div>
  <section class="admin-panel">
    <p class="muted">Showing the 50 most recent orders.</p>
    <c:choose><c:when test="${empty orders}"><p>No orders have been placed yet.</p></c:when><c:otherwise>
      <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Order</th><th>Customer</th><th>Delivery address</th><th>Date</th><th>Total</th><th>Status</th></tr></thead><tbody>
      <c:forEach var="order" items="${orders}"><tr>
        <td><a class="admin-text-link" href="${pageContext.request.contextPath}/admin/order-history?orderId=${order.id}">#<c:out value="${order.id}"/></a></td>
        <td><c:out value="${order.customerName}"/></td><td><c:out value="${order.deliveryAddress}"/></td>
        <td><c:out value="${order.createdAt}"/></td><td>₹<c:out value="${order.totalAmount}"/></td>
        <td><span class="admin-status"><c:out value="${order.status}"/></span></td>
      </tr></c:forEach></tbody></table></div>
    </c:otherwise></c:choose>
  </section>
</main>
</body></html>
