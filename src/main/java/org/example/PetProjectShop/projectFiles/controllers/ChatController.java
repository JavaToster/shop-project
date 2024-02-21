package org.example.PetProjectShop.projectFiles.controllers;

import org.example.PetProjectShop.projectFiles.models.Chat;
import org.example.PetProjectShop.projectFiles.models.Message;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.services.ChatService;
import org.example.PetProjectShop.projectFiles.services.MessageService;
import org.example.PetProjectShop.projectFiles.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        if(!personService.hasChat(username, chatId)){
            return "redirect:/chats";
        }
        model.addAttribute("messages", chatService.findMessagesByChatOfPerson(chatId));
        model.addAttribute("message", new Message());
        model.addAttribute("chatId", chatId);
        model.addAttribute("ownerUsername", username);
        model.addAttribute("interlocutor", chatService.findInterlocutorFromChat(username, chatService.findById(chatId)));

        return "html/chat/chat";
    }

    @PostMapping({"/{chatId}/message", "/{chatId}/message/"})
    public String sendMessage(@ModelAttribute("message") Message message, @PathVariable("chatId") int chatId, @RequestParam(value = "username") String ownerUsername){
        //Check if message text is empty

        if(messageService.messageIsEmpty(message)){
            return "redirect:/chats/"+chatId+"/";
        }

        //setting chat to message
        message.setChat(chatService.findById(chatId));
        //setting owner
        message.setOwner(personService.findByUsername(ownerUsername));

        //adding message
        messageService.addMessage(message);

        return "redirect:/chats/"+chatId+"/";
    }

    @GetMapping({"", "/"})
    public String index(@CookieValue("username") String username, Model model){
        List<Chat> chatsByUser = personService.findChatsByUsername(username);

        model.addAttribute("chats", chatService.findInterlocutorsFromChats(username, chatsByUser));
        model.addAttribute("chat", new Chat());

        //if user is shop -> return username of interlocutor
        //if user is just user -> return interlocutor shop name
        //shop can have chats with user(don't care have they shop or no)
        //user can have chats with shop -> we return this shops name
        model.addAttribute("userType", personService.isUserOfShop(username));

        return "/html/chat/index";
    }
}
