package org.example.PetProjectShop.projectFiles.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany
    @JoinTable(name = "chat_person",
    joinColumns = @JoinColumn(name = "chat_id"),
    inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> owners;

    @OneToMany(mappedBy = "chat")
    private List<Message> messages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chat(Person owner1, Person owner2){
        this.owners = new ArrayList<>(List.of(owner1, owner2));
    }

    public Chat(){}

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setOwners(List<Person> owners) {
        this.owners = owners;
    }

    public List<Person> getOwners() {
        return owners;
    }
}
