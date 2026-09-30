package applications.categories.book.seed;

import applications.categories.book.domain.CategoriesBook;
import applications.categories.book.persistence.CategoriesBookCommandRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CategoriesBookSeedListener {

    @Inject
    CategoriesBookCommandRepository commandRepository;

    @Transactional
    void onStart(@Observes StartupEvent event){
        for (var categories : CategoriesBookSeed.data()) {
            boolean exists = commandRepository
                    .find("slug", categories.slug())
                    .firstResultOptional()
                    .isPresent();

            if (!exists) {
                var entity = new CategoriesBook();
                entity.setName(categories.name());
                entity.setSlug(categories.slug());

                commandRepository.persist(entity);
            }
        }
    }
}
