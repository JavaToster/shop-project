package org.example.PetProjectShop.projectFiles.repositories;

import org.example.PetProjectShop.projectFiles.models.Comment;
import org.example.PetProjectShop.projectFiles.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByItem(Item item);
}
