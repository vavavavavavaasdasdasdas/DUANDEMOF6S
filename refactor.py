import os
import shutil
import re

base_dir = r'C:\DuAnTNDEMO\DemoDATN\src\main\java\com\example\demodatn'

mapping = {
    'sanpham': [
        'Product', 'ProductVariant', 'Category', 'Size',
        'AdminProductController', 'ProductController',
        'ProductRepository', 'ProductVariantRepository', 'CategoryRepository', 'SizeRepository',
        'ProductService'
    ],
    'khachhang': [
        'Customer', 'AdminCustomerController', 'CustomerRepository'
    ],
    'nhanvien': [
        'Employee', 'Role', 'User', 'AdminEmployeeController', 'EmployeeRepository', 'RoleRepository', 'UserRepository'
    ],
    'banhang': [
        'Order', 'OrderDetail', 'AdminOrderController', 'CartController', 'OrderController',
        'OrderDetailRepository', 'OrderRepository', 'OrderService', 'CartItem'
    ],
    'khuyenmai': [
        'Promotion', 'PromotionDetail', 'Voucher', 'AdminPromotionController', 'AdminVoucherController',
        'PromotionDetailRepository', 'PromotionRepository', 'VoucherRepository', 'VoucherService'
    ],
    'thongke': [
        'AdminController'
    ],
    'core': [
        'HomeController'
    ]
}

# Create dirs
for domain in mapping.keys():
    os.makedirs(os.path.join(base_dir, domain), exist_ok=True)

file_to_domain = {}
for domain, files in mapping.items():
    for f in files:
        file_to_domain[f] = domain

# Find all java files
java_files = []
for root, dirs, files in os.walk(base_dir):
    for f in files:
        if f.endswith('.java'):
            java_files.append(os.path.join(root, f))

# Move files
moved_files = []
for f_path in java_files:
    filename = os.path.basename(f_path)
    name = filename.replace('.java', '')
    if name in file_to_domain:
        domain = file_to_domain[name]
        new_path = os.path.join(base_dir, domain, filename)
        shutil.move(f_path, new_path)
        moved_files.append((name, domain, new_path))
        
print('Moved files:', len(moved_files))
