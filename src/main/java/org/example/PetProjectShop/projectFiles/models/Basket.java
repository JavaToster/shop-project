package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Basket")
public class Basket {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Person owner;

    @ManyToMany(mappedBy = "baskets")
    private List<Item> items;

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

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Basket(Person owner){
        this.owner = owner;
    }

    public Basket(){}
}
