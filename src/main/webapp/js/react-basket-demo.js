(function () {
  const mountNode = document.getElementById("react-basket-demo");
  if (!window.React || !window.ReactDOM) {
    mountNode.textContent = "The React demo could not load. Check your internet connection and refresh the page.";
    mountNode.setAttribute("role", "alert");
    return;
  }

  const h = window.React.createElement;
  const products = [
    { id: "milk", name: "Fresh milk", price: 32 },
    { id: "apples", name: "Apples", price: 120 },
    { id: "chips", name: "Potato chips", price: 20 }
  ];

  function BasketDemo() {
    const [quantities, setQuantities] = window.React.useState({});
    const itemCount = Object.values(quantities).reduce((sum, quantity) => sum + quantity, 0);
    const total = products.reduce((sum, product) => sum + product.price * (quantities[product.id] || 0), 0);

    function changeQuantity(productId, change) {
      setQuantities(function (current) {
        return Object.assign({}, current, {
          [productId]: Math.max(0, (current[productId] || 0) + change)
        });
      });
    }

    return h("section", { className: "react-demo-card", "aria-labelledby": "react-demo-title" },
      h("div", { className: "react-demo-heading" },
        h("div", null,
          h("h2", { id: "react-demo-title" }, "Build a quick basket"),
          h("p", null, "Try the controls—this interactive preview is powered by React and separate from your saved cart.")
        ),
        h("span", { className: "react-demo-badge" }, "React demo")
      ),
      h("div", { className: "react-demo-items" },
        products.map(function (product) {
          const quantity = quantities[product.id] || 0;
          return h("div", { className: "react-demo-row", key: product.id },
            h("div", { className: "react-demo-product" },
              h("strong", null, product.name),
              h("span", null, "₹" + product.price)
            ),
            h("div", { className: "react-demo-controls" },
              h("button", {
                type: "button",
                onClick: function () { changeQuantity(product.id, -1); },
                "aria-label": "Remove one " + product.name,
                disabled: quantity === 0
              }, "−"),
              h("output", { "aria-label": product.name + " quantity" }, quantity),
              h("button", {
                type: "button",
                onClick: function () { changeQuantity(product.id, 1); },
                "aria-label": "Add one " + product.name
              }, "+")
            )
          );
        })
      ),
      h("div", { className: "react-demo-footer" },
        h("div", { className: "react-demo-total", "aria-live": "polite" },
          "Total: ₹" + total,
          h("small", null, itemCount + (itemCount === 1 ? " item" : " items"))
        ),
        h("button", {
          type: "button",
          className: "react-demo-clear",
          onClick: function () { setQuantities({}); },
          disabled: itemCount === 0
        }, "Clear demo")
      )
    );
  }

  window.ReactDOM.createRoot(mountNode).render(h(BasketDemo));
})();
