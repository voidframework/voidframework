package sample.model;

import dev.voidframework.core.lang.CUID;
import dev.voidframework.persistence.hibernate.annotation.CuidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@Table(name = "EVENT")
public class EventModel {

    @Id
    @CuidGenerator
    @Column(name = "ID", updatable = false, nullable = false)
    private CUID id;

    @Column(name = "EVENT")
    private String event;

    public CUID getId() {
        return id;
    }

    public void setId(final CUID id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }
}
