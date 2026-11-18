<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html><html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>Frequently bought | QuickCart Admin</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head><body>
<%@ include file="/WEB-INF/jspf/admin-nav.jspf" %>
<main class="container admin-container">
  <div class="admin-page-heading"><div><p class="eyebrow">SALES INSIGHTS</p><h1>Frequently bought items</h1></div>
    <p class="muted">Ranked by total units sold in placed orders.</p></div>
  <section class="admin-panel">
    <div id="admin-popular-items">
      <c:choose><c:when test="${empty popularProducts}"><p class="muted">No placed orders yet, so there are no sales rankings.</p></c:when><c:otherwise>
        <div class="admin-table-wrap"><table class="admin-table"><thead><tr><th>Product</th><th>Category</th><th>Units sold</th><th>Orders</th><th>Stock</th></tr></thead><tbody>
        <c:forEach var="product" items="${popularProducts}">
          <tr class="popular-fallback-row"><td><c:out value="${product.name}"/></td><td><c:out value="${product.categoryName}"/></td><td><c:out value="${product.quantitySold}"/></td><td><c:out value="${product.orderCount}"/></td><td><c:out value="${product.stock}"/></td></tr>
        </c:forEach></tbody></table></div>
        <div hidden>
          <c:forEach var="product" items="${popularProducts}">
            <span data-popular-item data-name="<c:out value='${product.name}'/>"
              data-category="<c:out value='${product.categoryName}'/>"
              data-units="<c:out value='${product.quantitySold}'/>"
              data-orders="<c:out value='${product.orderCount}'/>"
              data-stock="<c:out value='${product.stock}'/>"></span>
          </c:forEach>
        </div>
      </c:otherwise></c:choose>
    </div>
  </section>
</main>
<script src="https://unpkg.com/react@18.3.1/umd/react.production.min.js"></script>
<script src="https://unpkg.com/react-dom@18.3.1/umd/react-dom.production.min.js"></script>
<script src="${pageContext.request.contextPath}/js/admin-popular-items.js"></script>
</body></html>
