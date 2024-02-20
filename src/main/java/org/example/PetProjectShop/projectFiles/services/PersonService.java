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
    public void setChatRepository(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    //for authentication
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
        //for adding image in person profile window
        person.setImageName(addImageForUser(image));
        //set role(shop or user)
        person.setRole(Role.ROLE_USER);
        //create new basket and favorite for him
        Basket basket = new Basket(person);
        Favorite favorite = new Favorite(person);
        //set basket and favorite for person
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

    //after authentication, we can understand who it is
    public void addCookieId(HttpServletResponse response, String username){
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


    // for get last id for person to set id for person profile image
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

    //for update user type from user to shop(when user open some shop)
    @Transactional
    public void personSetShop(int id){
        Person person = personRepository.findById(id).orElse(null);

        person.setRole(Role.ROLE_SHOP);

        personRepository.save(person);
    }

    //for delete cookie with user username, it's using when we log out from profile
    public void deleteCookies(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("user_id", "1");

        cookie.setMaxAge(0);

        httpServletResponse.addCookie(cookie);
    }

    //for check is user is owner some shop
    // if user is owner -> get "add item" button in shop window
    public boolean isOwner(String ownerUsername, String cookieUsername){
        return ownerUsername.equals(cookieUsername);
    }

    //for prune code
    public int getIdByUsername(String username){
        return personRepository.findByUsername(username).orElse(null).getId();
    }

    //check user is member of chat
    //if no -> redirect to profile, if yes get chat window
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

    //for getting chats of some person
    //when person open url "/chats", find chats by person username(get by cookie)
    @Transactional(readOnly = true)
    public List<Chat> findChatsByUsername(String username){
        Person person = personRepository.findByUsername(username).orElse(null);

        return person.getChats();
    }

    //for check person user type is shop
    @Transactional(readOnly = true)
    public boolean isUserOfShop(String username){
        Person person = personRepository.findByUsername(username).orElse(null);

        return person.getRoleOfEnumType() == Role.ROLE_SHOP;
    }
}
