package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Basket;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Optional<Basket> findByOwner(Person owner);
}
