package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Shop")
public class Shop {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "description")
    private String shopDescription;

    @Column(name = "country")
    private String country;

    @OneToMany(mappedBy = "shop")
    private List<Item> items;

    @ManyToMany(mappedBy = "shops", fetch = FetchType.EAGER)
    private List<Category> categories;

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
}
