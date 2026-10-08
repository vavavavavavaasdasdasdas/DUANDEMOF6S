package com.example.demodatn.sanpham;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.data.domain.*;
import java.util.*;

@Repository @RequiredArgsConstructor
public class AdminCatalogQuery {
    private final JdbcTemplate jdbc;
    public Page<Map<String,Object>> products(String keyword, Integer category, Boolean status, String sort, int page, int size) {
        String where = " WHERE 1=1";
        List<Object> args = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            where += " AND (p.name LIKE ? ESCAPE '!' OR p.code LIKE ? ESCAPE '!')";
            String pattern = "%" + keyword.trim().replace("!","!!").replace("%","!%").replace("_","!_").replace("[","![") + "%";
            args.add(pattern); args.add(pattern);
        }
        if (category != null) { where += " AND p.category_id=?"; args.add(category); }
        if (status != null) { where += " AND p.status=?"; args.add(status); }
        long count = jdbc.queryForObject("SELECT COUNT_BIG(*) FROM products p" + where, Long.class, args.toArray());
        int safeSize = Set.of(10,20,50).contains(size) ? size : 10;
        int safePage = Math.min(Math.max(0,page), (int) Math.max(0,(count-1)/safeSize));
        String order = switch(sort) {
            case "name" -> "p.name ASC,p.id DESC"; case "code" -> "p.code ASC,p.id DESC";
            case "price_asc" -> "v.price ASC,p.id DESC"; case "price_desc" -> "v.price DESC,p.id DESC";
            case "stock" -> "COALESCE(v.stock,0) ASC,p.id DESC"; default -> "p.id DESC";
        };
        args.add(safePage * safeSize); args.add(safeSize);
        String sql = "SELECT p.*,c.name AS category_name,v.price,v.max_price,COALESCE(v.stock,0) AS stock FROM products p LEFT JOIN categories c ON c.id=p.category_id LEFT JOIN (SELECT product_id,MIN(price) AS price,MAX(price) AS max_price,SUM(CAST(quantity AS bigint)) AS stock FROM product_variants GROUP BY product_id) v ON v.product_id=p.id" + where + " ORDER BY " + order + " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return new PageImpl<>(jdbc.queryForList(sql,args.toArray()),PageRequest.of(safePage,safeSize),count);
    }
    public List<Map<String,Object>> variants(Integer productId, String stock, int threshold) {
        return variantsPage(productId,stock,threshold,0,100).getContent();
    }
    public Page<Map<String,Object>> variantsPage(Integer productId, String stock, int threshold, int page, int size) {
        String sql = "SELECT v.id,v.product_id,v.size_id,p.code,p.name,s.size_value,v.price,v.quantity,v.status FROM product_variants v JOIN products p ON p.id=v.product_id JOIN sizes s ON s.id=v.size_id WHERE 1=1";
        List<Object> args = new ArrayList<>();
        if(productId != null) { sql += " AND v.product_id=?"; args.add(productId); }
        if("low".equals(stock)) { sql += " AND v.quantity > 0 AND v.quantity <= ?"; args.add(threshold); }
        if("out".equals(stock)) sql += " AND v.quantity=0";
        long count=jdbc.queryForObject("SELECT COUNT_BIG(*) "+sql.substring(sql.indexOf("FROM product_variants")),Long.class,args.toArray());
        int current=Math.min(Math.max(0,page),(int)Math.max(0,(count-1)/size));
        args.add(current*size); args.add(size);
        return new PageImpl<>(jdbc.queryForList(sql+" ORDER BY p.id DESC,s.size_value,v.id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",args.toArray()),PageRequest.of(current,size),count);
    }
    public List<Map<String,Object>> history(Integer variantId) {
        return jdbc.queryForList("SELECT TOP (100) l.*,p.name,s.size_value FROM inventory_logs l JOIN product_variants v ON v.id=l.variant_id JOIN products p ON p.id=v.product_id JOIN sizes s ON s.id=v.size_id " + (variantId == null ? "" : "WHERE l.variant_id=? ") + "ORDER BY l.created_at DESC,l.id DESC", variantId == null ? new Object[]{} : new Object[]{variantId});
    }
}

