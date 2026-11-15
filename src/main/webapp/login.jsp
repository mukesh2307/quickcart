<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String mode = (String) request.getAttribute("mode");
    boolean isRegister = "register".equals(mode);
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QuickCart - Login & Register</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="min-vh-100 bg-light d-flex flex-column">

    <!-- Navbar (Logo stays on /login so unauthenticated users cannot bypass login) -->
    <nav class="bg-warning py-3 shadow-sm">
        <div class="container d-flex justify-content-between align-items-center">
            <a href="<%= request.getContextPath() %>/login" class="text-decoration-none d-flex align-items-center gap-2">
                <span class="bg-dark text-warning fw-bold px-2 py-1 rounded fs-4">
                    Quick<span class="text-success">Cart</span>
                </span>
                <span class="badge rounded-pill bg-dark text-white">Online Store</span>
            </a>
            <span class="text-dark fw-semibold d-none d-sm-inline">
                <i class="bi bi-lightning-charge-fill me-1"></i>Groceries delivered in minutes
            </span>
        </div>
    </nav>

    <!-- Auth Card -->
    <main class="container my-auto py-5">
        <div class="row justify-content-center">
            <div class="col-12 col-sm-10 col-md-8 col-lg-5">
                <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
                    <div class="card-header bg-white border-bottom p-4 pb-3">
                        <div class="text-center mb-3">
                            <h1 class="h4 fw-bold text-dark mb-1">
                                <%= isRegister ? "Create Your Account" : "Welcome Back to QuickCart" %>
                            </h1>
                            <p class="text-muted small mb-0">
                                <%= isRegister ? "Sign up to browse fresh groceries & place instant orders" : "Log in to access your cart and fast checkout" %>
                            </p>
                        </div>

                        <!-- Mode Switcher -->
                        <div class="d-flex gap-2 bg-light p-1 rounded-3">
                            <a href="<%= request.getContextPath() %>/login"
                               class="btn w-50 fw-semibold rounded-2 <%= !isRegister ? "btn-success shadow-sm" : "btn-light text-muted" %>">
                                <i class="bi bi-box-arrow-in-right me-2"></i>Login
                            </a>
                            <a href="<%= request.getContextPath() %>/register"
                               class="btn w-50 fw-semibold rounded-2 <%= isRegister ? "btn-success shadow-sm" : "btn-light text-muted" %>">
                                <i class="bi bi-person-plus me-2"></i>Create Account
                            </a>
                        </div>
                    </div>

                    <div class="card-body p-4">
                        <% if (error != null) { %>
                            <div class="alert alert-danger py-2 small"><i class="bi bi-exclamation-triangle-fill me-2"></i><%= error %></div>
                        <% } %>
                        <% if (success != null) { %>
                            <div class="alert alert-success py-2 small"><i class="bi bi-check-circle-fill me-2"></i><%= success %></div>
                        <% } %>

                        <form method="post" action="<%= request.getContextPath() %><%= isRegister ? "/register" : "/login" %>">
                            <% if (isRegister) { %>
                                <div class="mb-3">
                                    <label class="form-label fw-medium">Full Name</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="bi bi-person text-muted"></i></span>
                                        <input type="text" name="fullName" class="form-control" placeholder="e.g., Aarav Sharma" required>
                                    </div>
                                </div>
                            <% } %>

                            <div class="mb-3">
                                <label class="form-label fw-medium">Email Address</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-envelope text-muted"></i></span>
                                    <input type="email" name="email" class="form-control" placeholder="you@example.com" required>
                                </div>
                            </div>

                            <% if (isRegister) { %>
                                <div class="mb-3">
                                    <label class="form-label fw-medium">Mobile Number</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light fw-semibold text-muted">+91</span>
                                        <input type="tel" name="phone" class="form-control" placeholder="9876543210" pattern="[0-9]{10}" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-medium">Default Delivery Address</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="bi bi-geo-alt text-muted"></i></span>
                                        <textarea name="deliveryAddress" class="form-control" rows="2" placeholder="Flat / House No., Street, City" required></textarea>
                                    </div>
                                </div>
                            <% } %>

                            <div class="mb-3">
                                <label class="form-label fw-medium">Password</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-lock text-muted"></i></span>
                                    <input type="password" name="password" class="form-control" placeholder="Enter your password" minlength="6" required>
                                </div>
                            </div>

                            <% if (isRegister) { %>
                                <div class="mb-4">
                                    <label class="form-label fw-medium">Confirm Password</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light"><i class="bi bi-shield-check text-muted"></i></span>
                                        <input type="password" name="confirmPassword" class="form-control" placeholder="Re-enter your password" minlength="6" required>
                                    </div>
                                </div>
                            <% } %>

                            <button type="submit" class="btn btn-success btn-lg w-100 fw-semibold shadow-sm">
                                <%= isRegister ? "Create Account" : "Login to Continue" %>
                                <i class="bi bi-arrow-right ms-2"></i>
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <footer class="bg-white border-top py-3 text-center text-muted small">
        <div class="container">QuickCart &mdash; Instant Grocery Delivery Store</div>
    </footer>
</body>
</html>