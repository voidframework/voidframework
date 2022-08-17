package sample.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.voidframework.validation.validator.TrimmedLength;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Simple POJO.
 */
public final class Pojo {

    /**
     * POJO's unique identifier.
     */
    public final String id = UUID.randomUUID().toString();

    /**
     * POJO's first name.
     */
    @TrimmedLength(min = 2)
    public final String firstName;

    /**
     * POJO's last name.
     */
    @NotNull
    @TrimmedLength(min = 2)
    public final String lastName;

    /**
     * Build a new instance.
     *
     * @param firstName The first name
     * @param lastName  The last name
     */
    @JsonCreator
    public Pojo(@JsonProperty("firstName") final String firstName,
                @JsonProperty("lastName") final String lastName) {

        this.firstName = firstName;
        this.lastName = lastName;
    }
}
