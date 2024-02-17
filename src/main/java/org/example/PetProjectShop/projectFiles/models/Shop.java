package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "Shop")
public class Shop {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shop_name")
    @NotEmpty(message = "Please write a name of shop")
    @Size(min = 3, max = 30, message = "min - 3, max - 30 characters")
    private String shopName;

    @Column(name = "description")
    @NotEmpty(message = "Please write a name of item")
    @Size(min = 1, max = 1000, message = "min - 100, max - 1000 characters")
    private String shopDescription;

    @Column(name = "country")
    @NotEmpty(message = "country can't to be empty")
    private String country;

    @OneToMany(mappedBy = "shop")
    private List<Item> items;

    @ManyToMany(mappedBy = "shops", fetch = FetchType.EAGER)
    private List<Category> categories;

    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public Shop(){}

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getShopDescription() {
        return shopDescription;
    }

    public void setShopDescription(String shopDescription) {
        this.shopDescription = shopDescription;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
