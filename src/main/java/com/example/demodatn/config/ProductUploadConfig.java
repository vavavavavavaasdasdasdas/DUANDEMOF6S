package com.example.demodatn.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.Path;

@Configuration
public class ProductUploadConfig implements WebMvcConfigurer {
    @Value("${shop.upload-dir:uploads/products}") private String directory;
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(directory).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/products/**").addResourceLocations(location.endsWith("/") ? location : location + "/");
    }
}

