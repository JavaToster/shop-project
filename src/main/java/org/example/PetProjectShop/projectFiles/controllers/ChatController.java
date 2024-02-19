package org.example.PetProjectShop.projectFiles.controllers;

import org.example.PetProjectShop.projectFiles.models.Chat;
import org.example.PetProjectShop.projectFiles.models.Message;
import org.example.PetProjectShop.projectFiles.services.ChatService;
import org.example.PetProjectShop.projectFiles.services.MessageService;
import org.example.PetProjectShop.projectFiles.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chats")
public class ChatController {

    private ChatService chatService;
    private PersonService personService;
    private MessageService messageService;

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping({"/{chatId}", "/{chatId}/"})
    public String showChat(Model model, @CookieValue("username") String username, @PathVariable("chatId") int chatId){
//        if(!personService.hasChat(username, chatId)){
//            return "redirect:/person";
//        }
        model.addAttribute("messages", personService.findMessagesByChatOfPerson(chatId));
        model.addAttribute("message", new Message());
        model.addAttribute("chatId", chatId);

        return "html/chat/chat";
    }

    @GetMapping({"", "/"})
    public String index(@CookieValue("username") String username, Model model){
        List<Chat> chatsByUser = personService.findChatsByUsername(username);

        model.addAttribute("chats", chatService.findInterlocutorsFromChats(username, chatsByUser));
        model.addAttribute("chat", new Chat());

        chatsByUser.get(0).getOwners().get(0);

        return "/html/chat/index";
    }

    @PostMapping({"/{chatId}/send-message", "/{chatId}/send-message/"})
    public String sendMessage(@ModelAttribute("message") Message message, @PathVariable("chatId") int chatId){
        message.setChat(chatService.findById(chatId));

        messageService.addMessage(message);

        return "redirect:/chats/"+chatId;
    }
}
