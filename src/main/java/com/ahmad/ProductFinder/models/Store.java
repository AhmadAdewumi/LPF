package com.ahmad.ProductFinder.models;

import com.ahmad.ProductFinder.embedded.Address;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "store_seq")
    @SequenceGenerator(name = "store_seq", sequenceName = "store_seq", allocationSize = 100)
    private Long id;


    @NotBlank(message = "store name cannot be empty")
    private String name;

    @NotNull(message = "store address cannot be empty")
    @Embedded
    private Address address;

    @Column(length = 1000)
    private String description;

    @CreationTimestamp
    @Column(nullable = false , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private boolean isVerified = true;

    private boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location; // 4326{spatial reference system identifier (SRID)

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "stores_products",
//            joinColumns = @JoinColumn(name ="store_id",referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "product_id",referencedColumnName = "id")
//    )
    @OneToMany(mappedBy = "store",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Product> products;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventory = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER , cascade = {CascadeType.DETACH , CascadeType.PERSIST , CascadeType.MERGE , CascadeType.REFRESH})
    @JoinTable(name = "store_tag", joinColumns = @JoinColumn(name = "store_id" , referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name ="tag_id" , referencedColumnName = "id")
    )
    private Collection<Tag> tags = new ArrayList<>();
}
