package com.example.service_backend.model;

import lombok.*;
import java.util.Objects;

import javax.persistence.*;

@Entity
@Generated
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class CartInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @NonNull
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="cart_id", nullable = false)
    private Cart cart;

    @NonNull
    @OneToOne
    @ToString.Exclude
    @JoinColumn(name="productId")
    private Product product;

    @NonNull
    private int quantity;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CartInfo)) {
            return false;
        }
        CartInfo cartLineInfo = (CartInfo) o;
        return Objects.equals(product, cartLineInfo.product) && quantity == cartLineInfo.quantity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity);
    }
    
}
