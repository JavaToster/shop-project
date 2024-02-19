package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

}
