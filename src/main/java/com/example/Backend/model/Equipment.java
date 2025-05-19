package com.example.Backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String category;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Column(name = "last_maintenance_date")
    private Date lastMaintenanceDate;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    @Column(name = "warranty_expiry")
    private Date warrantyExpiry;

    @OneToMany(mappedBy = "equipmentSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<MaintenanceSchedule> maintenanceSchedules;

    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(Date lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public Date getWarrantyExpiry() {
        return warrantyExpiry;
    }

    public void setWarrantyExpiry(Date warrantyExpiry) {
        this.warrantyExpiry = warrantyExpiry;
    }

    public List<MaintenanceSchedule> getMaintenanceSchedules() {
        return maintenanceSchedules;
    }

    public void setMaintenanceSchedules(List<MaintenanceSchedule> maintenanceSchedules) {
        this.maintenanceSchedules = maintenanceSchedules;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}