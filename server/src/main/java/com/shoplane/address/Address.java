package com.shoplane.address;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 40)
    private String label;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 32)
    private String phone;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(nullable = false, length = 80)
    private String state;

    @Column(nullable = false, length = 20)
    private String zip;

    @Column(nullable = false, length = 4)
    private String country;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }              public void setUserId(Long v) { this.userId = v; }
    public String getLabel() { return label; }              public void setLabel(String v) { this.label = v; }
    public String getFullName() { return fullName; }        public void setFullName(String v) { this.fullName = v; }
    public String getPhone() { return phone; }              public void setPhone(String v) { this.phone = v; }
    public String getStreet() { return street; }            public void setStreet(String v) { this.street = v; }
    public String getCity() { return city; }                public void setCity(String v) { this.city = v; }
    public String getState() { return state; }              public void setState(String v) { this.state = v; }
    public String getZip() { return zip; }                  public void setZip(String v) { this.zip = v; }
    public String getCountry() { return country; }          public void setCountry(String v) { this.country = v; }
    public boolean isDefault() { return isDefault; }        public void setDefault(boolean v) { this.isDefault = v; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
