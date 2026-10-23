package com.example.demodatn.service.donhang;
import com.example.demodatn.entity.sanpham.Product;
import com.example.demodatn.entity.sanpham.ProductVariant;
import com.example.demodatn.repository.sanpham.ProductRepository;
import com.example.demodatn.util.Pagination;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.RequiredArgsConstructor;
import java.util.*;

@Service @RequiredArgsConstructor
public class PosCatalogQuery {
    private final ProductRepository products;
    private final Pagination pagination;
    private final JdbcTemplate jdbc;
    public Page<Product> products(String q,Integer category,Object page) {
        org.springframework.data.jpa.domain.Specification<Product> filter=(root,query,cb)->{
            var variants=query.subquery(Integer.class);var v=variants.from(ProductVariant.class);
            variants.select(v.get("id")).where(cb.equal(v.get("product"),root),cb.isTrue(v.get("status")));
            var text=cb.or(cb.like(cb.lower(root.get("name")),like(q),'!'),cb.like(cb.lower(root.get("code")),like(q),'!'));
            var active=cb.and(cb.isTrue(root.get("status")),cb.exists(variants),text);
            return category==null?active:cb.and(active,cb.equal(root.get("category").get("id"),category));
        };
        return pagination.load(page,Sort.by("name","id"),request->products.findAll(filter,request));
    }
    public Page<Map<String,Object>> customers(String q,Object page) {
        String from=" FROM customers WHERE status=1 AND (LOWER(full_name) LIKE ? ESCAPE '!' OR phone LIKE ? ESCAPE '!')";
        return pagination.sql(jdbc,"SELECT id,full_name,phone"+from+" ORDER BY full_name,id","SELECT COUNT_BIG(*)"+from,List.of(like(q),like(q)),page);
    }
    private String like(String q){return "%"+q.trim().toLowerCase(Locale.ROOT).replace("!","!!").replace("%","!%").replace("_","!_").replace("[","![")+"%";}
}
