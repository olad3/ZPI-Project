package com.zpi.amoz.models;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID productOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.NEW;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address address;

    @OneToOne
    @JoinColumn(name = "invoiceId")
    private Invoice invoice;

    @Column(length = 10)
    private String trackingNumber;

    private LocalTime timeOfSending;

    @Column(nullable = false)
    private LocalDateTime timeOfCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductOrderItem> orderItems;

    public UUID getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(UUID productOrderId) {
        this.productOrderId = productOrderId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalTime getTimeOfSending() {
        return timeOfSending;
    }

    public void setTimeOfSending(LocalTime timeOfSending) {
        this.timeOfSending = timeOfSending;
    }

    public LocalDateTime getTimeOfCreation() {
        return timeOfCreation;
    }

    public void setTimeOfCreation(LocalDateTime timeOfCreation) {
        this.timeOfCreation = timeOfCreation;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<ProductOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<ProductOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public enum Status {
        NEW, ORDERED, SHIPPED, DELIVERED, CANCELLED
    }
}
