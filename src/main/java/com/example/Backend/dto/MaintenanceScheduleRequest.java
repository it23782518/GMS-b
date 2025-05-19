package com.example.Backend.dto;

import com.example.Backend.model.MaintenanceStatus;

import java.sql.Date;

public class MaintenanceScheduleRequest {
    private Long equipmentId;
    private String maintenanceType;
    private Date maintenanceDate;
    private String maintenanceDescription;
    private MaintenanceStatus status;
    private String technician;
    private Double maintenanceCost;

    // Getters
    public Long getEquipmentId() {
        return equipmentId;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public Date getMaintenanceDate() {
        return maintenanceDate;
    }

    public String getMaintenanceDescription() {
        return maintenanceDescription;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public String getTechnician() {
        return technician;
    }

    public Double getMaintenanceCost() {
        return maintenanceCost;
    }

    // Setters
    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public void setMaintenanceDate(Date maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public void setMaintenanceDescription(String maintenanceDescription) {
        this.maintenanceDescription = maintenanceDescription;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public void setMaintenanceCost(Double maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }
}
