package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Item;
import org.example.PetProjectShop.projectFiles.models.Shop;
import org.example.PetProjectShop.projectFiles.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByShop(Shop shop);

    List<Item> findByCategory(Category category);

    @Query(value = "select * from item where lower(item_name) like lower(:itemName)", nativeQuery = true)
    List<Item> findByItemNameLike(@Param("itemName") String itemName);

    List<Item> findByShopAndCategory(Shop shop, Category category);
}
