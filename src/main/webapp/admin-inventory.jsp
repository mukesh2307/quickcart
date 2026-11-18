<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Inventory | QuickCart Admin</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body>
<%@ include file="/WEB-INF/jspf/admin-nav.jspf" %>
<main class="container admin-container">
  <div class="admin-page-heading"><div><p class="eyebrow">CATALOGUE</p><h1>Inventory</h1></div><p class="muted">Update available stock for each product.</p></div>
  <c:if test="${not empty sessionScope.adminFlash}"><div class="notice"><c:out value="${sessionScope.adminFlash}"/></div><c:remove var="adminFlash" scope="session"/></c:if>
  <section class="admin-panel">
    <c:choose><c:when test="${empty products}"><p class="muted">No products found.</p></c:when><c:otherwise>
      <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Current stock</th><th>Update stock</th></tr></thead><tbody>
      <c:forEach var="product" items="${products}"><tr>
        <td><strong><c:out value="${product.name}"/></strong></td><td><c:out value="${product.categoryName}"/></td>
        <td>₹<c:out value="${product.price}"/></td><td><span class="${product.stock le 5 ? 'admin-low-stock' : ''}"><c:out value="${product.stock}"/></span></td>
        <td><form class="stock-form" method="post" action="${pageContext.request.contextPath}/admin/inventory">
          <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.adminCsrfToken}'/>">
          <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
          <label class="sr-only" for="stock-${product.id}">New stock for <c:out value="${product.name}"/></label>
          <input id="stock-${product.id}" type="number" name="stock" min="0" max="2147483647" value="<c:out value='${product.stock}'/>" required>
          <button type="submit" class="button button-dark">Save</button>
        </form></td>
      </tr></c:forEach></tbody></table></div>
    </c:otherwise></c:choose>
  </section>
</main>
</body></html>
