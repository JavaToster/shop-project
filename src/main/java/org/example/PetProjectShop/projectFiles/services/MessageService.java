package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.models.Message;
import org.example.PetProjectShop.projectFiles.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MessageService {

    private MessageRepository messageRepository;

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Transactional
    public void addMessage(Message message){
        messageRepository.save(message);
    }

    public boolean messageIsEmpty(Message message){
        return message.getMessageText().isEmpty();
    }
}
