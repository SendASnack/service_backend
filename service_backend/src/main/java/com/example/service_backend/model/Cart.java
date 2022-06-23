package com.example.service_backend.model;

import lombok.*;

import javax.persistence.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Generated
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="costumer_id", nullable = false)
    @NonNull
    private Costumer costumer;

    @OneToMany(mappedBy="cart", fetch=FetchType.LAZY ,orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<CartInfo> cartInfo = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Cart)) {
            return false;
        }
        Cart cart = (Cart) o;
        return Objects.equals(costumer, cart.costumer) && Objects.equals(cartInfo, cart.cartInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(costumer, cartInfo);
    }
    
}
