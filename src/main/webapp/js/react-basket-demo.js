(function () {
  const mountNode = document.getElementById("react-basket-demo");
  if (!window.React || !window.ReactDOM) {
    mountNode.textContent = "Frequently purchased items could not load. Check your internet connection and refresh the page.";
    mountNode.setAttribute("role", "alert");
    return;
  }

  const h = window.React.createElement;
  const products = Array.from(mountNode.querySelectorAll("[data-product-id]"), function (node) {
    return {
      id: node.dataset.productId,
      name: node.dataset.name,
      category: node.dataset.category,
      pricePaise: Math.round(Number(node.dataset.price) * 100),
      stock: Number(node.dataset.stock),
      inCart: Number(node.dataset.inCart)
    };
  });
  const cartUrl = mountNode.dataset.cartUrl;

  function BasketDemo() {
    const [quantities, setQuantities] = window.React.useState({});
    const selectedProducts = products.filter(function (product) {
      return (quantities[product.id] || 0) > 0;
    });
    const itemCount = selectedProducts.reduce(function (sum, product) {
      return sum + quantities[product.id];
    }, 0);
    const totalPaise = products.reduce(function (sum, product) {
      return sum + product.pricePaise * (quantities[product.id] || 0);
    }, 0);

    function changeQuantity(product, change) {
      setQuantities(function (current) {
        const nextQuantity = Math.max(0, Math.min(
          product.stock - product.inCart,
          (current[product.id] || 0) + change
        ));
        return Object.assign({}, current, {
          [product.id]: nextQuantity
        });
      });
    }

    return h("section", { className: "react-demo-card", "aria-labelledby": "react-demo-title" },
      h("div", { className: "react-demo-heading" },
        h("div", null,
          h("h2", { id: "react-demo-title" }, "Frequently purchased items"),
          h("p", null, "Choose quantities and add your picks to your cart.")
        )
      ),
      h("div", { className: "react-demo-items" },
        products.map(function (product) {
          const quantity = quantities[product.id] || 0;
          return h("div", { className: "react-demo-row", key: product.id },
            h("div", { className: "react-demo-product" },
              h("small", null, product.category),
              h("strong", null, product.name),
              h("span", null, "₹" + (product.pricePaise / 100).toFixed(2)),
              h("small", null, "In cart: " + product.inCart + " · " + (product.stock - product.inCart) + " available")
            ),
            h("div", { className: "react-demo-controls" },
              h("button", {
                type: "button",
                onClick: function () { changeQuantity(product, -1); },
                "aria-label": "Remove one " + product.name,
                disabled: quantity === 0
              }, "−"),
              h("output", { "aria-label": product.name + " quantity" }, quantity),
              h("button", {
                type: "button",
                onClick: function () { changeQuantity(product, 1); },
                "aria-label": "Add one " + product.name,
                disabled: quantity >= product.stock - product.inCart
              }, "+")
            )
          );
        })
      ),
      h("div", { className: "react-demo-footer" },
        h("div", { className: "react-demo-total", "aria-live": "polite" },
          "Selected: ₹" + (totalPaise / 100).toFixed(2),
          h("small", null, itemCount + (itemCount === 1 ? " item" : " items"))
        ),
        h("form", { method: "post", action: cartUrl, className: "react-demo-actions" },
          h("input", { type: "hidden", name: "action", value: "addSelected" }),
          selectedProducts.map(function (product) {
            return h(window.React.Fragment, { key: product.id },
              h("input", { type: "hidden", name: "productId", value: product.id }),
              h("input", { type: "hidden", name: "quantity", value: quantities[product.id] })
            );
          }),
          h("button", {
            type: "submit",
            className: "button button-dark",
            disabled: itemCount === 0
          }, "Add selected to cart"),
          h("button", {
            type: "button",
            className: "react-demo-clear",
            onClick: function () { setQuantities({}); },
            disabled: itemCount === 0
          }, "Clear selection")
        )
      )
    );
  }

  window.ReactDOM.createRoot(mountNode).render(h(BasketDemo));
})();
