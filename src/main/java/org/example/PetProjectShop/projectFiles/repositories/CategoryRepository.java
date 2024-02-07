package org.example.PetProjectShop.projectFiles.repositories;


import org.example.PetProjectShop.projectFiles.models.Category;
import org.example.PetProjectShop.projectFiles.models.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByShops(Shop shop);

}
