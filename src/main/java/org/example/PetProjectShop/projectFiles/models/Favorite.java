package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Favorite")
public class Favorite {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private Person owner;

    @ManyToMany(mappedBy = "favorites", cascade = CascadeType.REMOVE)
    private List<Item> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Favorite(Person person){
        this.owner = person;
    }

    public Favorite(){}

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
