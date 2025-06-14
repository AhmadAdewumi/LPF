package com.ahmad.ProductFinder.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;
}
