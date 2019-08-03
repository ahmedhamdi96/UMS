package com.sumerge.program.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "AUDITS", schema = "UMSDB")
public class Audit implements Serializable {
    @Id
    @Column(name = "AUDIT_ID")
    private Integer auditId;
    @Column(name = "AUTHOR")
    private String author;
    @Column(name = "ACTION")
    private String action;
    @Column(name = "TIMESTAMP")
    private Timestamp timestamp;
    @Column(name = "ENTITY_TYPE")
    private String entityType;
    @Column(name = "ENTITY")
    private String entity;

    public Audit() {
    }

    public Audit(String author, String action, Timestamp timestamp, String entityType, String entity) {
        this.author = author;
        this.action = action;
        this.timestamp = timestamp;
        this.entityType = entityType;
        this.entity = entity;
    }

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
