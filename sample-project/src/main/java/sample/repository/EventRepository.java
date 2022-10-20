package sample.repository;

import com.google.inject.Inject;
import com.google.inject.Provider;
import dev.voidframework.core.bindable.Repository;
import dev.voidframework.core.lang.CUID;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import sample.model.EventModel;

import java.util.List;
import java.util.Optional;

@Repository
public class EventRepository {

    private final Provider<EntityManager> entityManagerProvider;

    /**
     * Build a new instance.
     *
     * @param entityManagerProvider Instance of the provider "EntityManager"
     */
    @Inject
    public EventRepository(final Provider<EntityManager> entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    /**
     * Find a specific account by its unique identifier.
     *
     * @param id The unique identifier
     * @return The account
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Optional<EventModel> findById(final CUID id) {

        if (id == null) {
            return Optional.empty();
        }

        final List<EventModel> resultList = entityManagerProvider.get()
            .createQuery("SELECT x FROM EventModel x WHERE x.id = :id", EventModel.class)
            .setParameter("id", id)
            .setMaxResults(1)
            .getResultList();

        if (!resultList.isEmpty()) {
            return Optional.of(resultList.get(0));
        }

        return Optional.empty();
    }

    @Transactional
    public EventModel persist(final EventModel eventModel) {
        final EntityManager entityManager = entityManagerProvider.get();

        if (eventModel.getId() == null || entityManager.contains(eventModel)) {
            entityManager.persist(eventModel);
        } else {
            entityManager.merge(eventModel);
        }

        return eventModel;
    }
}
