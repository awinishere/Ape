package applications.categories.book.seed;

import java.util.List;

public final class CategoriesBookSeed {
    private CategoriesBookSeed(){}

    public static List<CategoriesBookData> data() {
        return List.of(
                new CategoriesBookData("Fiction", "fiction"),
                new CategoriesBookData("Fantasy", "fantasy"),
                new CategoriesBookData("Science Fiction", "science-fiction"),
                new CategoriesBookData("Romance", "romance"),
                new CategoriesBookData("Mystery", "mystery"),
                new CategoriesBookData("Horror", "horror"),
                new CategoriesBookData("Thriller", "thriller"),
                new CategoriesBookData("Adventure", "adventure"),
                new CategoriesBookData("Historical Fiction", "historical-fiction"),
                new CategoriesBookData("Biography", "biography"),
                new CategoriesBookData("History", "history"),
                new CategoriesBookData("Science", "science"),
                new CategoriesBookData("Philosophy", "philosophy"),
                new CategoriesBookData("Psychology", "psychology"),
                new CategoriesBookData("Education", "education"),
                new CategoriesBookData("Self-Help", "self-help"),
                new CategoriesBookData("Poetry", "poetry"),
                new CategoriesBookData("Drama", "drama"),
                new CategoriesBookData("Children's Books", "childrens-books"),
                new CategoriesBookData("Comics", "comics")
        );
    }
}
