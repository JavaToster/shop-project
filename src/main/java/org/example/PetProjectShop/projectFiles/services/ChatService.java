package org.example.PetProjectShop.projectFiles.services;


import org.example.PetProjectShop.projectFiles.models.Chat;
import org.example.PetProjectShop.projectFiles.models.Message;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.models.Shop;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChatService {
    private ChatRepository chatRepository;
    private PersonRepository personRepository;
    private ShopRepository shopRepository;
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

    public Chat findById(int chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    @Transactional
    public void createNewChat(int shopId, String username) {
        // step 1: getting shop and user by shop id and username
        Person shopOwner = shopRepository.findById(shopId).orElse(null).getPerson();
        Person user = personRepository.findByUsername(username).orElse(null);

        //step 2: create new chat with shop and user as owners
        Chat chat = new Chat(shopOwner, user);

        //step 3: adding this chat to shop and users chats list(Many to many)
        //the method "addChatToList" using for prune this code, you can see below
        shopOwner.setChats(addChatToList(shopOwner.getChats(), chat));
        user.setChats(addChatToList(user.getChats(), chat));

        //step 4: saving all
        chatRepository.save(chat);
        personRepository.save(user);
        personRepository.save(shopOwner);
    }

    // for find last chat id in table -> I create this to redirect this new creating chat
    public int getLastChatId(){

        return jdbcTemplate.query("SELECT id FROM Chat ORDER BY id DESC", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        }).stream().findAny().orElse(0);
    }

    //for find interlocutors from chat
    //When you open chats window, we want to get your interlocutors
    public List<Chat> findInterlocutorsFromChats(String username, List<Chat> chats){
        for(Chat chat: chats){
            //delete person when he has username -> because it's you
            chat.getOwners().removeIf(owner -> owner.getUsername().equals(username));
        }

        return chats;
    }

    //for prune code in methods
    private List<Chat> addChatToList(List<Chat> chats, Chat addingChat){
        if(chats == null){
            chats = new ArrayList<>(List.of(addingChat));
        }else{
            chats.add(addingChat);
        }

        return chats;
    }

    @Transactional(readOnly = true)
    public List<Message> findMessagesByChatOfPerson(int chatId){
        return chatRepository.findById(chatId).orElse(null).getMessages();
    }

    // find 1 interlocutor for 1 chat
    public String findInterlocutorFromChat(String username, Chat chat){
        for(Person owner: chat.getOwners()){
            if(!owner.getUsername().equals(username)){
                return owner.getUsername();
            }
        }
        return null;
    }

    // for check, if users have common chat
    public Optional<Chat> hasUsersAChat(Person shopUser, Person user){
        List<Chat> shopChats = shopUser.getChats();
        List<Chat> userChats = user.getChats();

        List<Chat> chatList = shopChats.stream()
                .filter(userChats::contains)
                .toList();

        return chatList.isEmpty() ? Optional.empty() : Optional.of(chatList.get(0));

    }
}
