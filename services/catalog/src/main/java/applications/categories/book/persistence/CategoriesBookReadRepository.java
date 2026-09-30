package applications.categories.book.persistence;

import applications.categories.book.domain.CategoriesBook;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CategoriesBookReadRepository
        implements PanacheRepositoryBase<CategoriesBook, UUID> {

    public List<CategoriesBook> findAllActive(){
        return list("active", true);
    }

    public List<CategoriesBook> findAllByName(String name){
        return list("name", name);
    }
}
