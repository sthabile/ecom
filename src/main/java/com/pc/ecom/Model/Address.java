package com.pc.ecom.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "addresses")
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    @Size(min = 5, message = "Street name must be at lest 5 characters")
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building Name must be at least 5 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "City Name must be at least 4 characters")
    private String city;

    @NotBlank
    @Size(min = 2, message = "Country must be at least 2 characters")
    private String country;

    @NotBlank
    @Size(min = 4, message = "Zipcode must be at least 4 characters")
    private String zipcode;

    public Address(String street, String buildingName, String city, String country, String zipcode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.country = country;
        this.zipcode = zipcode;
    }
}
