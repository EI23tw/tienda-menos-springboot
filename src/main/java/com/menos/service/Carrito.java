package com.menos.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class Carrito {
    
    private final Map<Long, Integer> items = new LinkedHashMap<>();

    public Map<Long, Integer> getItems() {
        return items;
    }

    public void cambiar(Long id, int delta) {
        int n = items.getOrDefault(id, 0) + delta;
        if (n <= 0) {
            items.remove(id);
        } else {
            items.put(id, n);
        }
    }

    public int count() {
        int suma = 0;
        // Bucle tradicional en lugar de stream().mapToInt().sum()
        for (Integer cantidad : items.values()) {
            suma = suma + cantidad;
        }
        return suma;
    }

    public void clear() {
        items.clear();
    }
}