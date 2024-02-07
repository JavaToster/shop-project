package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.models.Category;
import org.example.PetProjectShop.projectFiles.repositories.CategoryRepository;
import org.example.PetProjectShop.projectFiles.repositories.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private CategoryRepository categoryRepository;
    private ShopRepository shopRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public List<Category> findAllCategories(){
        return categoryRepository.findAll();
    }

    public List<Category> findCategoriesByShopId(int id){
        return categoryRepository.findByShops(shopRepository.findById(id).orElse(null));
    }

    public Category findById(int id){
        return categoryRepository.findById(id).orElse(null);
    }
}
