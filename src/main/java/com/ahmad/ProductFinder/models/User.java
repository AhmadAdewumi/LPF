package com.ahmad.ProductFinder.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.action.internal.OrphanRemovalAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
//    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 100)
    private Long id;

    @NotNull(message = "email cannot be empty")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "first name cannot be empty")
    private String firstName;

    @NotBlank(message = "last name cannot be empty")
    private String lastname;

    @NotNull(message = "username cannot be empty")
    @NaturalId
    private String username;

    @JsonIgnore
    private String password;

    @Column(
            nullable = false ,
            unique = true // phone number is unique per user
    )
    private String phoneNumber;

//    @NotNull(message = "roles cannot be empty")
//    @ElementCollection(fetch = FetchType.EAGER)
//    @Enumerated(EnumType.STRING)
//    private Collection<Roles> role = Set.of(Roles.USER);

    private boolean active = true; // useful for soft deletion

    @Column(nullable = false , updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "owner")
    private List<Store> store;

    @ManyToMany(fetch = FetchType.EAGER , cascade = {CascadeType.DETACH , CascadeType.PERSIST , CascadeType.MERGE , CascadeType.REFRESH})
    @JoinTable(name = "user_roles" , joinColumns = @JoinColumn(name = "user_id" , referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name ="role_id" , referencedColumnName = "id")
    )
    @JsonIgnore
    private Collection<Role> roles = new HashSet<>();
}
