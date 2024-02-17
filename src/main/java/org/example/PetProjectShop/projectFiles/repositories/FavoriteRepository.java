package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Favorite;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    Favorite findByOwner(Person owner);
}
