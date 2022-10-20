package dev.voidframework.persistence.hibernate;

import dev.voidframework.persistence.hibernate.cuid.CUIDType;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;

/**
 * Hibernate custom type contributor for Void Framework.
 */
public class CustomTypeContributor implements TypeContributor {

    @Override
    public void contribute(final TypeContributions typeContributions, final ServiceRegistry serviceRegistry) {

        typeContributions.contributeType(new CUIDType());
    }
}
