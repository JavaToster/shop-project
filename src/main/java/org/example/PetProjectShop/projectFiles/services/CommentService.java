package org.example.PetProjectShop.projectFiles.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.PetProjectShop.projectFiles.repositories.CommentRepository;
import org.example.PetProjectShop.projectFiles.repositories.ItemRepository;
import org.example.PetProjectShop.projectFiles.models.Comment;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {
    private CommentRepository commentRepository;

    private ItemRepository itemRepository;

    @Autowired
    public void setCommentRepository(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Comment> findByItem(int item){
        return commentRepository.findByItem(itemRepository.findById(item).orElse(null));
    }

    @Transactional
    public void addComment(int itemId, Comment comment){
        //add comment to item by itemId
        comment.setItem(itemRepository.findById(itemId).orElse(null));

        commentRepository.save(comment);
    }
}
