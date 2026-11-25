<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
<title>QuickCart | Groceries made easy</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/theme.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/react-demo.css">
</head><body>
<header class="topbar">
  <a class="brand" href="${pageContext.request.contextPath}/products"><span class="brand-mark">Q</span> quick<span>cart</span></a>
  <div class="delivery"><strong>Daily essentials, delivered</strong><small>Shop your everyday favourites</small></div>
  <nav class="topbar-links" aria-label="Store navigation">
    <a href="#categories">Categories</a>
    <a href="#catalogue">Shop products</a>
  </nav>
  <div class="customer-actions">
    <a class="cart-link" href="${pageContext.request.contextPath}/admin/login">Admin login</a>
    <a class="cart-link" href="${pageContext.request.contextPath}/cart">Go to cart<c:if test="${cartItemCount gt 0}"><span class="cart-count"><c:out value="${cartItemCount}"/></span></c:if></a>
    <form class="customer-logout-form" method="post" action="${pageContext.request.contextPath}/logout">
      <button class="cart-link customer-logout" type="submit">Log out</button>
    </form>
  </div>
</header>
<main class="container">
  <section class="hero">
    <div class="hero-copy-wrap"><p class="eyebrow">EVERYDAY ESSENTIALS</p><h1>Fresh groceries,<br><span>right to your door.</span></h1>
      <p class="hero-copy">Shop fresh produce, pantry staples and daily essentials.</p>
      <a class="button button-primary" href="#catalogue">Explore products ↓</a></div>
    <div class="hero-art" aria-hidden="true"><span>🥦</span><span>🍎</span><span>🥛</span><span>🥖</span></div>
  </section>
  <section id="categories" class="category-section" aria-labelledby="categories-title">
    <div class="section-heading"><div><p class="eyebrow">SHOP YOUR WAY</p><h2 id="categories-title">Browse categories</h2></div></div>
    <nav class="category-nav" aria-label="Product categories">
      <a class="category-chip ${selectedCategory eq 'all' ? 'is-active' : ''}" href="${pageContext.request.contextPath}/products"
         ${selectedCategory eq 'all' ? 'aria-current="page"' : ''}>All categories</a>
      <c:forEach var="cat" items="${categories}">
        <c:url var="categoryUrl" value="/products">
          <c:param name="category" value="${cat}"/>
          <c:param name="q" value="${search}"/>
        </c:url>
        <a class="category-chip ${selectedCategory eq cat ? 'is-active' : ''}" href="${categoryUrl}"
           ${selectedCategory eq cat ? 'aria-current="page"' : ''}>
          <span class="category-icon" aria-hidden="true"><c:choose>
            <c:when test="${cat eq 'Dairy'}">🥛</c:when>
            <c:when test="${cat eq 'Fruits'}">🍎</c:when>
            <c:when test="${cat eq 'Vegetables'}">🥬</c:when>
            <c:when test="${cat eq 'Snacks'}">🥨</c:when>
            <c:when test="${cat eq 'Beverages'}">🧃</c:when>
            <c:when test="${cat eq 'Bakery'}">🍞</c:when>
            <c:otherwise>🛒</c:otherwise>
          </c:choose></span><c:out value="${cat}"/>
        </a>
      </c:forEach>
    </nav>
  </section>
  <section class="promo-strip" aria-label="Shopping information">
    <div class="promo-item"><span class="promo-mark" aria-hidden="true">01</span><div><strong>Everyday essentials</strong><span>Browse the products you need, all in one place.</span></div></div>
    <div class="promo-item"><span class="promo-mark" aria-hidden="true">02</span><div><strong>Stock shown up front</strong><span>Check product availability before adding to cart.</span></div></div>
    <div class="promo-item"><span class="promo-mark" aria-hidden="true">03</span><div><strong>Simple checkout</strong><span>Review your cart and delivery details before ordering.</span></div></div>
  </section>
  <c:if test="${not empty sessionScope.flash}"><div class="notice"><c:out value="${sessionScope.flash}"/></div><c:remove var="flash" scope="session"/></c:if>
  <div id="react-basket-demo" data-cart-url="${pageContext.request.contextPath}/cart"
       data-search="<c:out value='${search}'/>" data-category="<c:out value='${selectedCategory}'/>" aria-live="polite">
    <c:forEach var="product" items="${products}">
      <c:set var="quickCartItem" value="${sessionScope.cart[product.id]}"/>
      <span hidden data-product-id="<c:out value='${product.id}'/>"
        data-name="<c:out value='${product.name}'/>"
        data-category="<c:out value='${product.categoryName}'/>"
        data-image-url="<c:out value='${product.thumbnailUrl}'/>"
        data-price="<c:out value='${product.price}'/>"
        data-stock="<c:out value='${product.stock}'/>"
        data-in-cart="<c:out value='${empty quickCartItem ? 0 : quickCartItem.quantity}'/>"></span>
    </c:forEach>
  </div>
  <section id="catalogue" class="catalogue">
    <div class="section-heading"><div><p class="eyebrow">THE DAILY PICKS</p><h2>Shop groceries</h2></div><span class="muted">${products.size()} products</span></div>
    <form class="filters" method="get" action="${pageContext.request.contextPath}/products">
      <input type="search" name="q" aria-label="Search products" placeholder="Search milk, fruits, snacks..." value="<c:out value='${search}'/>">
      <select name="category" aria-label="Filter by category"><option value="all">All categories</option>
        <c:forEach var="cat" items="${categories}"><option value="<c:out value='${cat}'/>" ${selectedCategory eq cat ? 'selected' : ''}><c:out value="${cat}"/></option></c:forEach>
      </select>
      <button class="button button-primary" type="submit">Search</button>
      <a class="reset-link" href="${pageContext.request.contextPath}/products">Reset</a>
    </form>
    <c:if test="${empty products}"><div class="empty-state"><h3>No products found</h3><p>Try another search or category.</p></div></c:if>
    <div class="product-grid">
      <c:forEach var="product" items="${products}">
        <article class="product-card">
          <div class="product-image-wrap"><img class="product-image" src="<c:out value='${product.thumbnailUrl}'/>" alt="<c:out value='${product.name}'/>" width="400" height="400" loading="lazy" decoding="async">
            <span class="category-pill"><c:out value="${product.categoryName}"/></span></div>
          <div class="product-info"><h3><c:out value="${product.name}"/></h3><p class="product-description"><c:out value="${product.description}"/></p>
            <div class="product-bottom"><div><strong class="price">₹<c:out value="${product.price}"/></strong><small class="stock"><c:out value="${product.stock}"/> in stock</small></div>
              <c:choose><c:when test="${product.stock gt 0}">
                <form method="post" action="${pageContext.request.contextPath}/cart">
                  <input type="hidden" name="action" value="add"><input type="hidden" name="productId" value="${product.id}">
                  <input type="hidden" name="q" value="<c:out value='${search}'/>">
                  <input type="hidden" name="category" value="<c:out value='${selectedCategory}'/>">
                  <button class="button button-add" type="submit">ADD +</button>
                </form>
              </c:when><c:otherwise><button class="button" disabled>Sold out</button></c:otherwise></c:choose>
            </div>
          </div>
        </article>
      </c:forEach>
    </div>
  </section>
  </main><footer class="footer">QuickCart · Everyday essentials, made simple.</footer>
  <script src="https://unpkg.com/react@18.3.1/umd/react.production.min.js"></script>
<script src="https://unpkg.com/react-dom@18.3.1/umd/react-dom.production.min.js"></script>
<script src="${pageContext.request.contextPath}/js/react-basket-demo.js"></script>
</body></html>
