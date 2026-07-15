package com.kashvi.ledger_system.dto;

import java.time.LocalDateTime;

public class HealthStatusResponse {

    private String backendStatus;
    private String databaseStatus;
    private String redisStatus;
    private LocalDateTime currentTime;
    private String applicationVersion;
    private String flywayMigrationVersion;

    public String getBackendStatus() {
        return backendStatus;
    }

    public String getDatabaseStatus() {
        return databaseStatus;
    }

    public String getRedisStatus() {
        return redisStatus;
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getFlywayMigrationVersion() {
        return flywayMigrationVersion;
    }

    public void setBackendStatus(String backendStatus) {
        this.backendStatus = backendStatus;
    }

    public void setDatabaseStatus(String databaseStatus) {
        this.databaseStatus = databaseStatus;
    }

    public void setRedisStatus(String redisStatus) {
        this.redisStatus = redisStatus;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public void setFlywayMigrationVersion(String flywayMigrationVersion) {
        this.flywayMigrationVersion = flywayMigrationVersion;
    }
}
