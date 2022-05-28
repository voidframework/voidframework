package com.voidframework.persistence.jpa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UNIT_TEST")
public class UnitTest {

    @Id
    @Column(name = "ID", nullable = false)
    public String id;
}
