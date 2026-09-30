package applications.categories.book.handler;

import applications.categories.book.command.CreateCategoriesBook;
import applications.categories.book.domain.CategoriesBook;
import applications.categories.book.persistence.CategoriesBookCommandRepository;
import applications.categories.book.persistence.CategoriesBookReadRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import shared.notification.ApplicationsNotification;

@ApplicationScoped
public class CreateCategoriesBookHandler {

    private static final Logger Log = Logger.getLogger(
            CreateCategoriesBookHandler.class
    );

    @Inject
    CategoriesBookCommandRepository commandRepository;

    @Inject
    CategoriesBookReadRepository readRepository;

    @Transactional
    public ApplicationsNotification handle(CreateCategoriesBook command) {
        try {
            if (readRepository.existsBySlug(command.slug())) {
                throw new WebApplicationException(
                        "Categories slug already exists",
                        Response.Status.CONFLICT
                );
            }

            CategoriesBook categoriesBook = new CategoriesBook();
            categoriesBook.setName(command.name());
            categoriesBook.setSlug(command.slug());

            commandRepository.persist(categoriesBook);


            return new ApplicationsNotification(
                    "Successfully added a new book category."
            );
        } catch (RuntimeException exception) {
            Log.errorf(
                    exception,
                    "Book category creation failed. Transaction will be rolled back: slug=%s",
                    command.slug()
            );

            throw exception;
        }
    }
}