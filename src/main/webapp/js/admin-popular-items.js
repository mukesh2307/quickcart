(function () {
  const mountNode = document.getElementById("admin-popular-items");
  if (!mountNode || !window.React || !window.ReactDOM) return;

  const products = Array.from(mountNode.querySelectorAll("[data-popular-item]"), function (node) {
    return {
      name: node.dataset.name,
      category: node.dataset.category,
      units: Number(node.dataset.units),
      orders: Number(node.dataset.orders),
      stock: Number(node.dataset.stock)
    };
  });
  if (products.length === 0) return;

  const h = window.React.createElement;

  function PopularItems() {
    const [sortBy, setSortBy] = window.React.useState("units");
    const [search, setSearch] = window.React.useState("");
    const visibleProducts = products
      .filter(function (product) {
        return product.name.toLowerCase().includes(search.trim().toLowerCase());
      })
      .sort(function (left, right) {
        return right[sortBy] - left[sortBy] || left.name.localeCompare(right.name);
      });

    return h("div", { className: "popular-react" },
      h("div", { className: "popular-controls" },
        h("label", null, "Find product ",
          h("input", {
            type: "search",
            value: search,
            onChange: function (event) { setSearch(event.target.value); },
            placeholder: "Search products"
          })
        ),
        h("label", null, "Rank by ",
          h("select", {
            value: sortBy,
            onChange: function (event) { setSortBy(event.target.value); }
          },
            h("option", { value: "units" }, "Units sold"),
            h("option", { value: "orders" }, "Number of orders")
          )
        )
      ),
      visibleProducts.length === 0
        ? h("p", { className: "muted" }, "No matching products.")
        : h("div", { className: "popular-grid" }, visibleProducts.map(function (product, index) {
            return h("article", { className: "popular-card", key: product.name },
              h("span", { className: "popular-rank" }, "#" + (index + 1)),
              h("p", { className: "eyebrow" }, product.category),
              h("h2", null, product.name),
              h("div", { className: "popular-sales" },
                h("strong", null, product.units),
                h("span", null, "units sold")
              ),
              h("p", { className: "popular-meta" },
                product.orders + (product.orders === 1 ? " order" : " orders") + " · " +
                product.stock + " in stock"
              )
            );
          }))
    );
  }

  window.ReactDOM.createRoot(mountNode).render(h(PopularItems));
})();
