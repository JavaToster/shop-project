package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;

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
}
