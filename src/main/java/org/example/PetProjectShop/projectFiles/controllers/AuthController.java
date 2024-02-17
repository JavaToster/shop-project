package org.example.PetProjectShop.projectFiles.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private PersonService personService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/login", "/login/"})
    public String loginPage(HttpServletResponse httpServletResponse){
        return "/html/auth/login";
    }

    @GetMapping({"/register", "/register/"})
    public String registrationPage(@ModelAttribute("person") Person person, @RequestParam(value = "error", required = false) Integer errorId, Model model){
        //error id: 1-username error, 2 - image error
        if(errorId == null) {
            return "/html/auth/registration";
        }else if(errorId == 1){
            model.addAttribute("usernameError", true);
        }else if(errorId == 2){
            model.addAttribute("imageError", true);
        }

        return "/html/auth/registration";
    }

    @PostMapping({"/register", "/register/"})
    public String registrationPagePost(@RequestParam("username")String username, @RequestParam("password") String password, @RequestParam("image")MultipartFile image){
        if(personService.userIsEmpty(username)){
            return "redirect:/auth/register?error=1";
        }

        try {
            personService.register(username, passwordEncoder.encode(password), image);
        }catch (StringIndexOutOfBoundsException e){
            return "redirect:/auth/register?error=2 ";
        }

        return "redirect:/auth/login";
    }

}
