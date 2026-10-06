import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator {
    public static void main(String[] args) throws IOException {
        String[] repos = {"Customer", "Employee", "Category", "Size", "Product", "ProductVariant", "Voucher", "Promotion", "PromotionDetail", "Order", "OrderDetail"};
        for (String r : repos) {
            String content = "package com.example.demodatn.repository;\n\n" +
                             "import com.example.demodatn.entity." + r + ";\n" +
                             "import org.springframework.data.jpa.repository.JpaRepository;\n\n" +
                             "public interface " + r + "Repository extends JpaRepository<" + r + ", Integer> {\n" +
                             "}\n";
            write("src/main/java/com/example/demodatn/repository/" + r + "Repository.java", content);
        }
        System.out.println("Repositories created!");
    }

    static void write(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(file);
        writer.write(content);
        writer.close();
    }
}
