package org.example.PetProjectShop.projectFiles.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.PetProjectShop.projectFiles.models.Item;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.models.Shop;
import org.example.PetProjectShop.projectFiles.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/person")
public class PersonController {

    private ItemService itemService;
    private PersonService personService;
    private BasketService basketService;
    private FavoriteService favoriteService;
    private PasswordEncoder passwordEncoder;
    private ShopService shopService;
    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Autowired
    public void setBasketService(BasketService basketService) {
        this.basketService = basketService;
    }

    @Autowired
    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setShopService(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping({"/basket", "/basket/"})
    public String showBasket(@CookieValue("username") String username, Model model){
        model.addAttribute("items", itemService.findItemsByBasketId(basketService.findBasketByPerson(personService.getIdByUsername(username)).getId()));
        model.addAttribute("item", new Item());

        System.out.println(itemService.findItemsByBasketId(basketService.findBasketByPerson(personService.findByUsername(username).getId()).getId()));

        return "/html/basket/showBasket";
    }

    @GetMapping({"/favorite", "/favorite/"})
    public String showFavorite(@CookieValue("username") String username, Model model){
        model.addAttribute("items", itemService.findItemsByFavoriteId(favoriteService.findByPerson(personService.getIdByUsername(username)).getId()));
        model.addAttribute("item", new Item());

        return "/html/favorite/showFavorite";
    }
//
//    @GetMapping({"/showCookieBasket", "/showCookieBasket/"})
//    public String showPersonCookieBasket(@CookieValue("username") String username){
//        return "redirect:/person/"+userId+"/basket";
//    }
//
//    @GetMapping({"/showCookieFavorite", "/showCookieFavorite/"})
//    public String showPersonCookieFavorite(@CookieValue("user_id") String userId){
//        return "redirect:/person/"+userId+"/favorite";
//    }

    @GetMapping("/addToBasket")
    public String addToBasket(@CookieValue("username") String username, @RequestParam("itemId") int itemId){

        basketService.addItem(personService.getIdByUsername(username), itemId);

        return "redirect:/shop/item/"+itemId+"/";
    }

    @GetMapping("/addToFavorite")
    public String addToFavorite(@CookieValue("username") String username, @RequestParam("itemId") int itemId){
        favoriteService.addItem(personService.getIdByUsername(username),  itemId);

        return "redirect:/shop/item/"+itemId+"/";
    }

    @GetMapping({"/", ""})
    public String show(Model model, @CookieValue("username") String username){
        model.addAttribute("user", personService.findById(personService.getIdByUsername(username)));

        return "/html/account/showAccount";
    }

    @GetMapping("/delete")
    public String deleteAccount(@CookieValue("username") String username){
        personService.delete(personService.getIdByUsername(username));

        return "redirect:/auth/logout";
    }

    @GetMapping("/changePassword")
    public String changePassword(Model model, @RequestParam("part") int part, @CookieValue("username") String username){
        // 304-проверка пароля, 404-изменение пароля, 500-неправильный пароль при проверки пароля

        model.addAttribute("person", new Person());
        model.addAttribute("userId", personService.getIdByUsername(username));

        if(part == 304){
            return "/html/auth/checkPassword";
        }

        return "redirect:/person/changePassword?part=304&error";
    }

    @PostMapping("/{id}/changePassword/checkPassword")
    public String checkPassword(@PathVariable("id") int id, @ModelAttribute("person") Person updatedPerson, Model model){
        String password = personService.loadUserByUsername(personService.findById(id).getUsername()).getPassword();

//        password --- $2a$10$NseuJkACVU4ZRioOlXc5buA11ETDKJxwe4cnGaQtXcQf7mSVEoyrS
//        password --- $2a$10$vbp0tUpk9O4eeNzeCJoaF.kxvT3538dPha3O8CtN5KeJzzavms9fK

        if(passwordEncoder.matches(updatedPerson.getPassword(), password)){
            model.addAttribute("userId", id);

            return "/html/auth/changePassword";
        }

        return "redirect:/person/changePassword?part=500";
    }

    @PostMapping("/{id}/changePassword")
    public String changePassword(@PathVariable("id") int id, @ModelAttribute("person") Person updatedPerson){
        Person person = personService.findById(id);

        person.setPassword(passwordEncoder.encode(updatedPerson.getPassword()));

        personService.save(person);

        return "redirect:/person/";
    }

    @GetMapping("/create-a-shop")
    public String createShop(@CookieValue("username") String username, Model model){
        model.addAttribute("userId", personService.getIdByUsername(username));
        model.addAttribute("shop", new Shop());

        return "/html/shop/newShop";
    }

    @PostMapping("/{id}/create-a-shop")
    public String createShopPost(@PathVariable("id") int id, @ModelAttribute("shop") @Valid Shop shop, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "/html/shop/newShop";
        }

        personService.personSetShop(id);
        shopService.add(id, shop);

        return "redirect:/person/";
    }
}
