import os

def write_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

print("Patching files...")
write_file("C:/DuAnTNDEMO/DemoDATN/src/main/resources/static/css/style.css", """
body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background-color: #f8f9fa;
}
.hero-banner {
    background: url('https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80') center/cover no-repeat;
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    text-shadow: 2px 2px 4px rgba(0,0,0,0.7);
}
.hero-banner h1 {
    font-size: 3rem;
    font-weight: bold;
}
.product-card {
    transition: transform 0.3s;
    border: none;
    border-radius: 10px;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}
.product-card:hover {
    transform: translateY(-5px);
}
.admin-card {
    border-radius: 10px;
    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    padding: 20px;
    margin-bottom: 20px;
    background: white;
}
""")

write_file("C:/DuAnTNDEMO/DemoDATN/src/main/resources/templates/user/home.html", """
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Shoe Store</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="/">Premium Shoes</a>
        <div class="collapse navbar-collapse">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="/">Home</a></li>
                <li class="nav-item"><a class="nav-link" href="/products">Shop</a></li>
                <li class="nav-item"><a class="nav-link" href="/cart">Cart</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="hero-banner">
    <div>
        <h1>Step Up Your Game</h1>
        <p class="lead">Discover the latest trends in footwear.</p>
        <a href="/products" class="btn btn-primary btn-lg">Shop Now</a>
    </div>
</div>

<div class="container mt-5">
    <h2 class="text-center mb-4">Featured Products</h2>
    <div class="row">
        <!-- Products will go here -->
        <div class="col-md-3 mb-4" th:each="p : ${products}">
            <div class="card product-card">
                <div class="card-body text-center">
                    <h5 class="card-title" th:text="${p.name}">Product Name</h5>
                    <p class="text-danger fw-bold" th:text="${p.price + ' VNĐ'}">Price</p>
                    <a th:href="@{'/products/' + ${p.id}}" class="btn btn-outline-dark w-100">View Details</a>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('success')) {
        Swal.fire('Success', 'Action completed successfully!', 'success');
    }
    if (urlParams.has('error')) {
        Swal.fire('Error', urlParams.get('error'), 'error');
    }
</script>
</body>
</html>
""")

write_file("C:/DuAnTNDEMO/DemoDATN/src/main/resources/templates/admin/dashboard.html", """
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/admin">Admin Dashboard</a>
    </div>
</nav>
<div class="container-fluid mt-4">
    <div class="row">
        <div class="col-md-3">
            <div class="list-group">
                <a href="/admin" class="list-group-item list-group-item-action active">Dashboard</a>
                <a href="/admin/orders" class="list-group-item list-group-item-action">Orders</a>
                <a href="/admin/products" class="list-group-item list-group-item-action">Products</a>
                <a href="/admin/vouchers" class="list-group-item list-group-item-action">Vouchers</a>
                <a href="/admin/promotions" class="list-group-item list-group-item-action">Promotions</a>
            </div>
        </div>
        <div class="col-md-9">
            <div class="row">
                <div class="col-md-3">
                    <div class="admin-card text-center text-white bg-primary">
                        <h5>Total Revenue</h5>
                        <h3 th:text="${totalRevenue}">0</h3>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="admin-card text-center text-white bg-success">
                        <h5>Total Orders</h5>
                        <h3 th:text="${totalOrders}">0</h3>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="admin-card text-center text-white bg-warning">
                        <h5>Pending Orders</h5>
                        <h3 th:text="${pendingOrders}">0</h3>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="admin-card text-center text-white bg-danger">
                        <h5>Low Stock</h5>
                        <h3 th:text="${lowStock}">0</h3>
                    </div>
                </div>
            </div>
            <div class="admin-card mt-4">
                <canvas id="revenueChart"></canvas>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script th:inline="javascript">
    const ctx = document.getElementById('revenueChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
            datasets: [{
                label: 'Revenue',
                data: [12000, 19000, 3000, 5000, 20000, 30000],
                borderColor: '#0d6efd',
                tension: 0.1
            }]
        }
    });
</script>
</body>
</html>
""")

print("Done patching.")
