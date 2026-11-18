<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin dashboard | QuickCart</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body>
<%@ include file="/WEB-INF/jspf/admin-nav.jspf" %>
<main class="container admin-container">
  <div class="admin-page-heading"><div><p class="eyebrow">QUICKCART OPERATIONS</p><h1>Admin dashboard</h1></div>
    <p class="muted">Signed in as <c:out value="${sessionScope.adminEmail}"/></p></div>
  <div class="admin-metrics">
    <a class="admin-metric" href="${pageContext.request.contextPath}/admin/inventory"><span>Products</span><strong><c:out value="${productCount}"/></strong><small>Manage catalogue stock</small></a>
    <a class="admin-metric" href="${pageContext.request.contextPath}/admin/inventory"><span>Low stock</span><strong><c:out value="${lowStockCount}"/></strong><small>5 units or fewer</small></a>
    <a class="admin-metric" href="${pageContext.request.contextPath}/admin/orders"><span>Recent orders</span><strong><c:out value="${recentOrderCount}"/></strong><small>View order list</small></a>
  </div>
  <section class="admin-panel">
    <div class="admin-section-heading"><div><p class="eyebrow">LATEST ACTIVITY</p><h2>Recent orders</h2></div>
      <a class="admin-text-link" href="${pageContext.request.contextPath}/admin/orders">View all recent orders</a></div>
    <c:choose><c:when test="${empty recentOrders}"><p class="muted">No orders have been placed yet.</p></c:when><c:otherwise>
      <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Order</th><th>Customer</th><th>Date</th><th>Total</th><th>Status</th></tr></thead><tbody>
      <c:forEach var="order" items="${recentOrders}"><tr>
        <td><a class="admin-text-link" href="${pageContext.request.contextPath}/admin/order-history?orderId=${order.id}">#<c:out value="${order.id}"/></a></td>
        <td><c:out value="${order.customerName}"/></td><td><c:out value="${order.createdAt}"/></td>
        <td>₹<c:out value="${order.totalAmount}"/></td><td><span class="admin-status"><c:out value="${order.status}"/></span></td>
      </tr></c:forEach></tbody></table></div>
    </c:otherwise></c:choose>
  </section>
  <div class="admin-shortcuts">
    <a href="${pageContext.request.contextPath}/admin/inventory"><strong>Manage inventory</strong><span>Update stock levels across the catalogue.</span></a>
    <a href="${pageContext.request.contextPath}/admin/popular-items"><strong>Frequently bought</strong><span>See top products by units sold.</span></a>
    <a href="${pageContext.request.contextPath}/admin/order-history"><strong>Order history</strong><span>Review past orders and their items.</span></a>
  </div>
</main>
</body></html>
