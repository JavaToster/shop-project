package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

}
