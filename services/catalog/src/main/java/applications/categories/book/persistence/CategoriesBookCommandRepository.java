package applications.categories.book.persistence;

import applications.categories.book.domain.CategoriesBook;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class CategoriesBookCommandRepository
        implements PanacheRepositoryBase<CategoriesBook, UUID> {

}
