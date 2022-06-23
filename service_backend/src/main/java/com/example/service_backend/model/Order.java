package com.example.service_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Generated
@Entity
@Table(name="orders")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    
    private double totalPrice;

    @ManyToMany
    @ToString.Exclude
    private List<Product> products = new ArrayList<>();

    @JoinColumn(name="costumerId")
    private String costumer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(order.totalPrice, totalPrice) == 0 && Objects.equals(id, order.id) && Objects.equals(costumer, order.costumer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, totalPrice, costumer);
    }
}
