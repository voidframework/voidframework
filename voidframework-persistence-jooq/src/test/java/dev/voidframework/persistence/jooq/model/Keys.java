/*
 * This file is generated by jOOQ.
 */
package dev.voidframework.persistence.jooq.model;


import dev.voidframework.persistence.jooq.model.tables.UnitTestManagedEntity;
import dev.voidframework.persistence.jooq.model.tables.records.UnitTestManagedEntityRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in
 * PUBLIC.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<UnitTestManagedEntityRecord> CONSTRAINT_C = Internal.createUniqueKey(UnitTestManagedEntity.UNIT_TEST_MANAGED_ENTITY, DSL.name("CONSTRAINT_C"), new TableField[] { UnitTestManagedEntity.UNIT_TEST_MANAGED_ENTITY.ID }, true);
}
