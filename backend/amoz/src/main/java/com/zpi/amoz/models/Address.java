package com.zpi.amoz.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID addressId;

    @Column(length = 255)
    private String city;

    @Column(length = 255)
    private String street;

    @Column(length = 10)
    private String streetNumber;

    @Column(length = 10)
    private String apartmentNumber;

    @Column(length = 10)
    private String postalCode;

    @Column(nullable = false, length = 255)
    private String additionalInformation;

    @OneToMany(mappedBy = "defaultDeliveryAddress")
    private List<Customer> customers;

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}