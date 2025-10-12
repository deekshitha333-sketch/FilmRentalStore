//package com.example.filmRental.Entity;
//
//import jakarta.persistence.*;
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "customer")
//public class Customer {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "customer_id")
//    private Short customerId;
//
//    @Column(name = "store_id", nullable = false)
//    private Short storeId;
//
//    @Column(name = "first_name", nullable = false, length = 45)
//    private String firstName;
//
//    @Column(name = "last_name", nullable = false, length = 45)
//    private String lastName;
//
//    @Column(length = 50)
//    private String email;
//
//    @Column(name = "address_id", nullable = false)
//    private Short addressId;
//
//    @Column(nullable = false)
//    private Boolean active = Boolean.TRUE;
//
//    @Column(name = "create_date", nullable = false, updatable = false)
//    private LocalDateTime createDate;
//
//    @Column(name = "last_update")
//    private Timestamp lastUpdate;
//
//    // getters/setters
//    public Short getCustomerId() { return customerId; }
//    public void setCustomerId(Short customerId) { this.customerId = customerId; }
//
//    public Short getStoreId() { return storeId; }
//    public void setStoreId(Short storeId) { this.storeId = storeId; }
//
//    public String getFirstName() { return firstName; }
//    public void setFirstName(String firstName) { this.firstName = firstName; }
//
//    public String getLastName() { return lastName; }
//    public void setLastName(String lastName) { this.lastName = lastName; }
//
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//
//    public Short getAddressId() { return addressId; }
//    public void setAddressId(Short addressId) { this.addressId = addressId; }
//
//    public Boolean getActive() { return active; }
//    public void setActive(Boolean active) { this.active = active; }
//
//    public LocalDateTime getCreateDate() { return createDate; }
//    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }
//
//    public Timestamp getLastUpdate() { return lastUpdate; }
//    public void setLastUpdate(Timestamp lastUpdate) { this.lastUpdate = lastUpdate; }
//}
package com.example.filmRental.Entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short customer_id;

    @Column(nullable = false)
    private Short store_id; // TINYINT UNSIGNED in DB; map as Short for simplicity

    @Column(nullable = false, length = 45)
    private String first_name;

    @Column(nullable = false, length = 45)
    private String last_name;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    private Short address_id;

    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(nullable = false)
    private LocalDateTime create_date;

    @Column
    private Timestamp last_update;

    public Short getCustomer_id() { return customer_id; }
    public void setCustomer_id(Short customer_id) { this.customer_id = customer_id; }

    public Short getStore_id() { return store_id; }
    public void setStore_id(Short store_id) { this.store_id = store_id; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Short getAddress_id() { return address_id; }
    public void setAddress_id(Short address_id) { this.address_id = address_id; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public LocalDateTime getCreate_date() { return create_date; }
    public void setCreate_date(LocalDateTime create_date) { this.create_date = create_date; }

    public Timestamp getLast_update() { return last_update; }
    public void setLast_update(Timestamp last_update) { this.last_update = last_update; }
}
