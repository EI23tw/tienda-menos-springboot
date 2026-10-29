package com.menos.web;

import com.menos.service.Carrito;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModel {
    
    private final Carrito c;

    public GlobalModel(Carrito c) {
        this.c = c;
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return c.count();
    }
}