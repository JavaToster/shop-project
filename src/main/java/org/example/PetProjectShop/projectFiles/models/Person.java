package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.example.PetProjectShop.projectFiles.util.Role;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @OneToOne(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private Basket basket;
    @OneToOne(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private Favorite favorite;
    @Column(name = "imagename")
    private String imageName;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(mappedBy = "person", cascade = CascadeType.REMOVE)
    private Shop shop;

    public Person(String username, String password){
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        basket.setOwner(this);
        this.basket = basket;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public void setFavorite(Favorite favorite) {
        this.favorite = favorite;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Person(){}

    public String getRole(){
        if(this.role == Role.ROLE_USER){
            return "ROLE_USER";
        }

        return "ROLE_SHOP";
    }

    public void setRole(Role role){
        this.role = role;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
