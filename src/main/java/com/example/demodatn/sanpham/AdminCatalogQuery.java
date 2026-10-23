package com.example.demodatn.sanpham;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.domain.*;
import java.util.*;

@Repository @RequiredArgsConstructor
public class AdminCatalogQuery {
    private final JdbcTemplate jdbc;
    private final com.example.demodatn.core.Pagination pagination;
    public Page<Map<String,Object>> products(String keyword, Integer category, Boolean status, String sort, Object page) {
        String where = " WHERE 1=1";
        List<Object> args = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            where += " AND (p.name LIKE ? ESCAPE '!' OR p.code LIKE ? ESCAPE '!')";
            String pattern = "%" + keyword.trim().replace("!","!!").replace("%","!%").replace("_","!_").replace("[","![") + "%";
            args.add(pattern); args.add(pattern);
        }
        if (category != null) { where += " AND p.category_id=?"; args.add(category); }
        if (status != null) { where += " AND p.status=?"; args.add(status); }
        String countSql = "SELECT COUNT_BIG(*) FROM products p" + where;
        String order = switch(sort) {
            case "name" -> "p.name ASC,p.id DESC"; case "code" -> "p.code ASC,p.id DESC";
            case "price_asc" -> "v.price ASC,p.id DESC"; case "price_desc" -> "v.price DESC,p.id DESC";
            case "stock" -> "COALESCE(v.stock,0) ASC,p.id DESC"; default -> "p.id DESC";
        };
        String sql = "SELECT p.*,c.name AS category_name,v.price,v.max_price,COALESCE(v.stock,0) AS stock FROM products p LEFT JOIN categories c ON c.id=p.category_id LEFT JOIN (SELECT product_id,MIN(price) AS price,MAX(price) AS max_price,SUM(CAST(quantity AS bigint)) AS stock FROM product_variants GROUP BY product_id) v ON v.product_id=p.id" + where + " ORDER BY " + order;
        return pagination.sql(jdbc,sql,countSql,args,page);
    }
    public List<Map<String,Object>> variants(Integer productId, String stock, int threshold) {
        return variantsPage(productId,stock,threshold,0).getContent();
    }
    public Page<Map<String,Object>> variantsPage(Integer productId, String stock, int threshold, Object page) {
        return variantsPage(productId,stock,threshold,page,null);
    }
    public Page<Map<String,Object>> variantsPage(Integer productId, String stock, int threshold, Object page, Integer materialId) {
        String sql = "SELECT v.id,v.product_id,v.size_id,v.chat_lieu_id,p.code,p.name,s.size_value,m.ten AS material_name,v.price,v.quantity,v.status FROM product_variants v JOIN products p ON p.id=v.product_id JOIN sizes s ON s.id=v.size_id JOIN chat_lieu m ON m.id=v.chat_lieu_id WHERE 1=1";
        List<Object> args = new ArrayList<>();
        if(materialId != null) { sql += " AND v.chat_lieu_id=?"; args.add(materialId); }
        if(productId != null) { sql += " AND v.product_id=?"; args.add(productId); }
        if("low".equals(stock)) { sql += " AND v.quantity > 0 AND v.quantity <= ?"; args.add(threshold); }
        if("out".equals(stock)) sql += " AND v.quantity=0";
        return pagination.sql(jdbc,sql+" ORDER BY p.id DESC,s.size_value,v.id","SELECT COUNT_BIG(*) "+sql.substring(sql.indexOf("FROM product_variants")),args,page);
    }
    public List<Map<String,Object>> history(Integer variantId){return history(variantId,0).getContent();}
    public Page<Map<String,Object>> history(Integer variantId,Object page) { return history(variantId,page,null); }
    public Page<Map<String,Object>> history(Integer variantId,Object page,Integer materialId) {
        String from=" FROM inventory_logs l JOIN product_variants v ON v.id=l.variant_id JOIN products p ON p.id=v.product_id JOIN sizes s ON s.id=v.size_id JOIN chat_lieu m ON m.id=v.chat_lieu_id WHERE 1=1 ";
        List<Object> args=new ArrayList<>();
        if(variantId!=null){from+=" AND l.variant_id=? ";args.add(variantId);}
        if(materialId!=null){from+=" AND v.chat_lieu_id=? ";args.add(materialId);}
        return pagination.sql(jdbc,"SELECT l.*,p.name,s.size_value,m.ten AS material_name"+from+" ORDER BY l.created_at DESC,l.id DESC","SELECT COUNT_BIG(*)"+from,args,page);
    }
}

