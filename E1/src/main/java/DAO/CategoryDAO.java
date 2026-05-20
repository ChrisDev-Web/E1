/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import Models.Category;
import Repositories.CategoryRepository;
import Repositories.ICategoryRepository;
import java.util.List;

public class CategoryDAO {
    private final ICategoryRepository repository;

    // Al instanciar el DAO, automáticamente se vincula con el repositorio de categorías
    public CategoryDAO() {
        this.repository = new CategoryRepository();
    }

    public List<Category> listar() {
        return repository.getAll();
    }

    public boolean registrar(Category cat) {
        return repository.add(cat);
    }

    public boolean modificar(Category cat) {
        return repository.update(cat);
    }

    public boolean eliminar(int id) {
        return repository.delete(id);
    }
}