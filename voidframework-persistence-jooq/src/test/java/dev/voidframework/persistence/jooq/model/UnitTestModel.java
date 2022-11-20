package dev.voidframework.persistence.jooq.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "UNIT_TEST_MANAGED_ENTITY")
public class UnitTestModel {

    @Id
    @Column(name = "ID", nullable = false)
    public String id;
}
