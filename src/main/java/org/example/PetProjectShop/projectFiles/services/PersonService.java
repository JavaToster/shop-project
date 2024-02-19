package org.example.PetProjectShop.projectFiles.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.PetProjectShop.projectFiles.models.*;
import org.example.PetProjectShop.projectFiles.repositories.*;
import org.example.PetProjectShop.projectFiles.security.PersonDetails;
import org.example.PetProjectShop.projectFiles.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService implements UserDetailsService {

    private PersonRepository personRepository;
    private BasketRepository basketRepository;
    private FavoriteRepository favoriteRepository;
    private ChatOwnerRepository chatOwnerRepository;
    private ChatRepository chatRepository;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setBasketRepository(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Autowired
    public void setFavoriteRepository(FavoriteRepository favoriteRepository){
        this.favoriteRepository = favoriteRepository;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    public void setChatOwnerRepository(ChatOwnerRepository chatOwnerRepository) {
        this.chatOwnerRepository = chatOwnerRepository;
    }

    @Autowired
    public void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUsername(username);

        if(person.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        return new PersonDetails(person.get());
    }

    @Transactional
    public void register(String username, String password, MultipartFile image){
        Person person = new Person(username, password);
        person.setImageName(addImageForUser(image));
        person.setRole(Role.ROLE_USER);
        Basket basket = new Basket(person);
        Favorite favorite = new Favorite(person);
        person.setBasket(basket);
        person.setFavorite(favorite);
        personRepository.save(person);
        basketRepository.save(basket);
        favoriteRepository.save(favorite);
    }

    public Person findByUsername(String username){
        return personRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void delete(int userId){
        Person person = personRepository.findById(userId).orElse(null);

        personRepository.delete(person);
    }

    @Transactional(readOnly = true)
    public Person get(String username){
        return personRepository.findByUsername(username).orElse(null);
    }

    public void addCookieId(HttpServletResponse response, String username){
        Person person = personRepository.findByUsername(username).get();

        Cookie cookie = new Cookie("username", username);
        cookie.setMaxAge(60*60);

        response.addCookie(cookie);
    }

    @Transactional(readOnly = true)
    public Person findById(int id){
        return personRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Person person) {
        personRepository.save(person);
    }

    public int getLastIdForPerson(){
        return jdbcTemplate.query("SELECT id FROM Person ORDER BY id DESC", new RowMapper<Integer>(){
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        }).stream().findAny().orElse(0);
    }

    public String addImageForUser(MultipartFile file){
        String path = "C:/Java/PetProjectShop/src/main/resources/static/images/user/";
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.indexOf(".");
        //file_id.jpeg/others
        String imageName = (getLastIdForPerson()+1)+fileName.substring(dotIndex);

        try {
            file.transferTo(new File(path + imageName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageName;
    }

    public boolean userIsEmpty(String username){
        Optional<Person> person = personRepository.findByUsername(username);

        return person.isPresent();
    }

    @Transactional
    public void personSetShop(int id){
        Person person = personRepository.findById(id).get();

        person.setRole(Role.ROLE_SHOP);

        personRepository.save(person);
    }

    public void deleteCookies(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("user_id", "1");

        cookie.setMaxAge(0);

        httpServletResponse.addCookie(cookie);
    }

    public boolean isOwner(String ownerUsername, String cookieUsername){
        return ownerUsername.equals(cookieUsername);
    }

    public int getIdByUsername(String username){
        return personRepository.findByUsername(username).orElse(null).getId();
    }

    @Transactional(readOnly = true)
    public List<Message> findMessagesByChatOfPerson(int chatId){
        return chatRepository.findById(chatId).orElse(null).getMessages();
    }

//    @Transactional(readOnly = true)
//    public ChatOwner findChatOwnerByChatId(int chatId, List<ChatOwner> owners){
//        for(ChatOwner owner: owners){
//            if(owner.getChat().getId() == chatId){
//                return owner;
//            }
//        }
//    }

    @Transactional
    public boolean hasChat(String username, int chatId){
        Person person = personRepository.findByUsername(username).orElse(null);

        for(Chat chat: person.getChats()){
            if(chatId == chat.getId()){
                return true;
            }
        }

        return false;
    }

    @Transactional(readOnly = true)
    public List<Chat> findChatsByUsername(String username){
        Person person = personRepository.findByUsername(username).orElse(null);

        return person.getChats();
    }

//    private List<Chat> findChatsByOwners(List<ChatOwner> owners){
//        List<Chat> chats = new ArrayList<>();
//
//        for (ChatOwner owner: owners){
//            chats.add(owner.getChat());
//        }
//
//        return chats;
//    }
}
