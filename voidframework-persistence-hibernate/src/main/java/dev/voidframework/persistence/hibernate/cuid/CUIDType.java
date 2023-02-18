package dev.voidframework.persistence.hibernate.cuid;

import dev.voidframework.core.lang.CUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * Value type mapper for {@code CUID}.
 *
 * @since 1.3.0
 */
public final class CUIDType implements UserType<CUID> {

    @Override
    public int getSqlType() {

        return Types.VARCHAR;
    }

    @Override
    public Class<CUID> returnedClass() {

        return CUID.class;
    }

    @Override
    public boolean equals(final CUID x, final CUID y) {

        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final CUID x) {

        return Objects.hashCode(x);
    }

    @Override
    public CUID nullSafeGet(final ResultSet rs,
                            final int position,
                            final SharedSessionContractImplementor session,
                            final Object owner) throws SQLException {

        if (rs.wasNull()) {
            return null;
        }

        final String columnValue = (String) rs.getObject(position);
        return CUID.fromString(columnValue);
    }

    @Override
    public void nullSafeSet(final PreparedStatement st,
                            final CUID value,
                            final int index,
                            final SharedSessionContractImplementor session) throws SQLException {

        if (value == null) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, value.toString());
        }
    }

    @Override
    public CUID deepCopy(final CUID value) {

        return value; // Immutable
    }

    @Override
    public boolean isMutable() {

        return false;
    }

    @Override
    public Serializable disassemble(final CUID value) {

        return deepCopy(value);
    }

    @Override
    public CUID assemble(final Serializable cached, final Object owner) {

        return deepCopy((CUID) cached);
    }

    @Override
    public CUID replace(final CUID detached, final CUID managed, final Object owner) {

        return deepCopy(detached);
    }
}
