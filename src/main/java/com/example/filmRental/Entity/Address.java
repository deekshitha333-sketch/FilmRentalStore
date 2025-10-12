package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short address_id;

    @Column(nullable = false, length = 50)
    private String address;

    @Column(length = 50)
    private String address2;

    @Column(nullable = false, length = 20)
    private String district;

    @Column(nullable = false)
    private Short city_id;

    @Column(length = 10)
    private String postal_code;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private Timestamp last_update;

    public Short getAddress_id() { return address_id; }
    public void setAddress_id(Short address_id) { this.address_id = address_id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public Short getCity_id() { return city_id; }
    public void setCity_id(Short city_id) { this.city_id = city_id; }
    public String getPostal_code() { return postal_code; }
    public void setPostal_code(String postal_code) { this.postal_code = postal_code; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Timestamp getLast_update() { return last_update; }
    public void setLast_update(Timestamp last_update) { this.last_update = last_update; }
}