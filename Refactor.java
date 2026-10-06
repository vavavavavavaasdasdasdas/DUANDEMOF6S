import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Refactor {
    public static void main(String[] args) throws Exception {
        String baseStr = "src/main/java/com/example/demodatn";
        Path baseDir = Paths.get(baseStr);

        Map<String, List<String>> mapping = new HashMap<>();
        mapping.put("sanpham", Arrays.asList("Product", "ProductVariant", "Category", "Size", "AdminProductController", "ProductController", "ProductRepository", "ProductVariantRepository", "CategoryRepository", "SizeRepository", "ProductService"));
        mapping.put("khachhang", Arrays.asList("Customer", "AdminCustomerController", "CustomerRepository"));
        mapping.put("nhanvien", Arrays.asList("Employee", "Role", "User", "AdminEmployeeController", "EmployeeRepository", "RoleRepository", "UserRepository"));
        mapping.put("banhang", Arrays.asList("Order", "OrderDetail", "AdminOrderController", "CartController", "OrderController", "OrderDetailRepository", "OrderRepository", "OrderService", "CartItem"));
        mapping.put("khuyenmai", Arrays.asList("Promotion", "PromotionDetail", "Voucher", "AdminPromotionController", "AdminVoucherController", "PromotionDetailRepository", "PromotionRepository", "VoucherRepository", "VoucherService"));
        mapping.put("thongke", Arrays.asList("AdminController"));
        mapping.put("core", Arrays.asList("HomeController"));

        Map<String, String> classToDomain = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : mapping.entrySet()) {
            Files.createDirectories(baseDir.resolve(entry.getKey()));
            for (String cls : entry.getValue()) {
                classToDomain.put(cls, entry.getKey());
            }
        }

        List<Path> javaFiles = new ArrayList<>();
        Files.walk(baseDir).filter(p -> p.toString().endsWith(".java") && !p.toString().contains("Refactor.java")).forEach(javaFiles::add);

        Map<String, String> oldFqcnToNewFqcn = new HashMap<>();
        Map<String, String> newPackages = new HashMap<>();

        for (Path p : javaFiles) {
            String name = p.getFileName().toString().replace(".java", "");
            if (classToDomain.containsKey(name)) {
                String domain = classToDomain.get(name);
                String oldPkg = getPackage(p);
                String newPkg = "com.example.demodatn." + domain;
                oldFqcnToNewFqcn.put(oldPkg + "." + name, newPkg + "." + name);
                newPackages.put(name, newPkg);
                
                Path newPath = baseDir.resolve(domain).resolve(p.getFileName());
                Files.move(p, newPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved " + name + " to " + newPath);
            }
        }

        // Now update imports and package declarations
        javaFiles.clear();
        Files.walk(baseDir).filter(p -> p.toString().endsWith(".java") && !p.toString().contains("Refactor.java")).forEach(javaFiles::add);

        for (Path p : javaFiles) {
            List<String> lines = Files.readAllLines(p, StandardCharsets.UTF_8);
            List<String> newLines = new ArrayList<>();
            String name = p.getFileName().toString().replace(".java", "");
            for (String line : lines) {
                // Fix package
                if (line.startsWith("package ") && newPackages.containsKey(name)) {
                    line = "package " + newPackages.get(name) + ";";
                }
                
                // Fix imports
                for (Map.Entry<String, String> entry : oldFqcnToNewFqcn.entrySet()) {
                    if (line.contains("import " + entry.getKey() + ";")) {
                        line = line.replace("import " + entry.getKey() + ";", "import " + entry.getValue() + ";");
                    }
                    // Generic replace for fully qualified uses in code if any, but import mostly
                }
                newLines.add(line);
            }
            Files.write(p, newLines, StandardCharsets.UTF_8);
        }
        System.out.println("Refactoring completed.");
    }

    static String getPackage(Path p) throws Exception {
        for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            if (line.startsWith("package ")) {
                return line.replace("package ", "").replace(";", "").trim();
            }
        }
        return "";
    }
}
