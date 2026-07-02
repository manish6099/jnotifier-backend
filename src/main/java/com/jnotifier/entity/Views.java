package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "views")
public class Views extends BaseEntity {
    @NotBlank
    @Column(name = "ip_address", columnDefinition = "VARCHAR(16)")
    private String ipAddress;

    public Views() {
    }

    public Views(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
