package org.example.PetProjectShop.projectFiles.services;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.PetProjectShop.projectFiles.models.Basket;
import org.example.PetProjectShop.projectFiles.models.Favorite;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.repositories.BasketRepository;
import org.example.PetProjectShop.projectFiles.repositories.FavoriteRepository;
import org.example.PetProjectShop.projectFiles.repositories.PersonRepository;
import org.example.PetProjectShop.projectFiles.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PersonService implements UserDetailsService {

    private PersonRepository personRepository;
    private BasketRepository basketRepository;
    private FavoriteRepository favoriteRepository;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUsername(username);

        if(person.isEmpty()){
            throw new UsernameNotFoundException("User not found!");
        }

        return new PersonDetails(person.get());
    }

    @Transactional
    public void register(Person person){
        Basket basket = new Basket(person);
        Favorite favorite = new Favorite(person);
        person.setBasket(basket);
        person.setFavorite(favorite);
        personRepository.save(person);
        basketRepository.save(basket);
        favoriteRepository.save(favorite);
    }

    public void addCookieId(HttpServletResponse response, String username){
        Person person = personRepository.findByUsername(username).get();

        Cookie cookie = new Cookie("user_id", ""+person.getId());
        cookie.setMaxAge(60*60);

        response.addCookie(cookie);
    }
}
