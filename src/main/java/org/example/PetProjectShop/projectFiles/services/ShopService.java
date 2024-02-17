package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.repositories.*;
import org.example.PetProjectShop.projectFiles.models.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ShopService {

    private ShopRepository shopRepository;
    private CategoryRepository categoryRepository;
    private PersonRepository personRepository;

    @Autowired
    public void setShopRepository(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Shop findById(int id){
        return shopRepository.findById(id).orElse(null);
    }

    @Transactional
    public void add(int personId, Shop shop){
        shop.setPerson(personRepository.findById(personId).orElse(null));

        shopRepository.save(shop);
    }

    @Transactional
    public void addCategory(Shop shop, Category category){
        Hibernate.initialize(shop.getCategories());
        if(shop.getCategories() == null){
            shop.setCategories(new ArrayList<>(List.of(category)));
            category.getShops().add(shop);
            shopRepository.save(shop);
            categoryRepository.save(category);
            return;
        }
        shop.getCategories().add(category);
        category.getShops().add(shop);
        shopRepository.save(shop);
        categoryRepository.save(category);
    }
}
