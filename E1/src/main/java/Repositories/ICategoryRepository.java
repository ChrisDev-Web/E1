package Repositories;

import Models.Category;
import java.util.List;

public interface ICategoryRepository {
    List<Category> getAll();
    boolean add(Category category);
    boolean update(Category category);
    boolean delete(int id);
}