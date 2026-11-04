<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>QuickCart | Groceries made easy</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head><body>
<header class="topbar">
  <a class="brand" href="${pageContext.request.contextPath}/products"><span class="brand-mark">Q</span> quick<span>cart</span></a>
  <div class="delivery"><strong>Daily essentials, delivered</strong><small>Shop your everyday favourites</small></div>
  <a class="cart-link" href="${pageContext.request.contextPath}/cart">🛒 Cart</a>
</header>
<main class="container">
  <section class="hero">
    <div><p class="eyebrow">EVERYDAY ESSENTIALS</p><h1>Your groceries.<br><span>One quick stop.</span></h1>
      <p class="hero-copy">Fresh picks, pantry staples and snacks at your fingertips.</p>
      <a class="button button-dark" href="#catalogue">Explore products ↓</a></div>
    <div class="hero-art" aria-hidden="true"><span>🥦</span><span>🍎</span><span>🥛</span><span>🥖</span></div>
  </section>
  <c:if test="${not empty sessionScope.flash}"><div class="notice"><c:out value="${sessionScope.flash}"/></div><c:remove var="flash" scope="session"/></c:if>
  <section id="catalogue" class="catalogue">
    <div class="section-heading"><div><p class="eyebrow">THE DAILY PICKS</p><h2>Shop groceries</h2></div><span class="muted">${products.size()} products</span></div>
    <form class="filters" method="get" action="${pageContext.request.contextPath}/products">
      <input type="search" name="q" placeholder="Search milk, fruits, snacks..." value="<c:out value='${search}'/>">
      <select name="category" aria-label="Filter by category"><option value="all">All categories</option>
        <c:forEach var="cat" items="${categories}"><option value="<c:out value='${cat}'/>" ${selectedCategory eq cat ? 'selected' : ''}><c:out value="${cat}"/></option></c:forEach>
      </select>
      <button class="button button-dark" type="submit">Search</button>
      <a class="reset-link" href="${pageContext.request.contextPath}/products">Reset</a>
    </form>
    <c:if test="${empty products}"><div class="empty-state"><h3>No products found</h3><p>Try another search or category.</p></div></c:if>
    <div class="product-grid">
      <c:forEach var="product" items="${products}">
        <article class="product-card">
          <div class="product-image-wrap"><img class="product-image" src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>" loading="lazy">
            <span class="category-pill"><c:out value="${product.categoryName}"/></span></div>
          <div class="product-info"><h3><c:out value="${product.name}"/></h3><p class="product-description"><c:out value="${product.description}"/></p>
            <div class="product-bottom"><div><strong class="price">₹<c:out value="${product.price}"/></strong><small class="stock"><c:out value="${product.stock}"/> in stock</small></div>
              <c:choose><c:when test="${product.stock gt 0}">
                <form method="post" action="${pageContext.request.contextPath}/cart">
                  <input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="${product.id}">
                  <button class="button button-add" type="submit">ADD +</button>
                </form>
              </c:when><c:otherwise><button class="button" disabled>Sold out</button></c:otherwise></c:choose>
            </div>
          </div>
        </article>
      </c:forEach>
    </div>
  </section>
</main><footer class="footer">QuickCart · A Java JSP + JDBC college project</footer>
</body></html>
