package org.example.PetProjectShop.projectFiles.services;


import org.example.PetProjectShop.projectFiles.models.Chat;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.repositories.ChatOwnerRepository;
import org.example.PetProjectShop.projectFiles.repositories.ChatRepository;
import org.example.PetProjectShop.projectFiles.repositories.PersonRepository;
import org.example.PetProjectShop.projectFiles.repositories.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ChatService {
    private ChatRepository chatRepository;
    private PersonRepository personRepository;
    private ShopRepository shopRepository;
    private ChatOwnerRepository chatOwnerRepository;
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setShopRepository(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Autowired
    public void setChatOwnerRepository(ChatOwnerRepository chatOwnerRepository) {
        this.chatOwnerRepository = chatOwnerRepository;
    }

    public Chat findById(int chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    @Transactional
    public void createNewChat(int shopId, String username) {
        Person shopOwner = shopRepository.findById(shopId).orElse(null).getPerson();
        Person user = personRepository.findByUsername(username).orElse(null);

        Chat chat = new Chat(shopOwner, user);

        shopOwner.setChats(addChatToList(shopOwner.getChats(), chat));
        user.setChats(addChatToList(user.getChats(), chat));

        chatRepository.save(chat);
        personRepository.save(user);
        personRepository.save(shopOwner);
    }
    public int getLastChatId(){

        return jdbcTemplate.query("SELECT id FROM Chat ORDER BY id DESC", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        }).stream().findAny().orElse(0);
    }

//    public List<String> findMemberNameFromChatsList(String username, List<Chat> chats){
//
//    }

    private List<Optional<Person>> findMembersFromChatList(List<Chat> chats, String username){
        List<Optional<Person>> members = new ArrayList<>();

        for(Chat chat: chats){
            for(Person member: chat.getOwners()){
                if(!member.getUsername().equals(username)){
                    members.add(Optional.of(member));
                }
            }
        }

        return members;
    }

    public List<Chat> findInterlocutorsFromChats(String username, List<Chat> chats){
        for(Chat chat: chats){
            chat.getOwners().removeIf(owner -> owner.getUsername().equals(username));
        }

        return chats;
    }

    private List<Chat> addChatToList(List<Chat> chats, Chat addingChat){
        if(chats == null){
            chats = new ArrayList<>(List.of(addingChat));
        }else{
            chats.add(addingChat);
        }

        return chats;
    }
}
