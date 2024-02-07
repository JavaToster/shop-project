package org.example.PetProjectShop.projectFiles.controllers;

import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private PersonService personService;

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping({"/login", "/login/"})
    public String loginPage(){
        return "/html/auth/login";
    }

    @GetMapping({"/register", "/register/"})
    public String registrationPage(@ModelAttribute("person") Person person){
        return "/html/auth/registration";
    }

    @PostMapping({"/register", "/register/"})
    public String registrationPagePost(@ModelAttribute("person") Person person){
        personService.register(person);

        return "redirect:/auth/login";
    }
}
