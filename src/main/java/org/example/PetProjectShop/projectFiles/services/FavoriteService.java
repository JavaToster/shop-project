package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.models.Favorite;
import org.example.PetProjectShop.projectFiles.models.Item;
import org.example.PetProjectShop.projectFiles.models.Person;
import org.example.PetProjectShop.projectFiles.repositories.FavoriteRepository;
import org.example.PetProjectShop.projectFiles.repositories.ItemRepository;
import org.example.PetProjectShop.projectFiles.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class FavoriteService {

    private FavoriteRepository favoriteRepository;
    private PersonRepository personRepository;
    private ItemRepository itemRepository;

    @Autowired
    public void setFavoriteRepository(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Autowired
    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public void save(Favorite favorite){
        favoriteRepository.save(favorite);
    }

    public Favorite findByPerson(int personId){
        return favoriteRepository.findByOwner(personRepository.findById(personId).orElse(null));
    }

    @Transactional
    public void addItem(int personId, int itemId){
        Favorite favorite = favoriteRepository.findByOwner(personRepository.findById(personId).orElse(null));
        Item item = itemRepository.findById(itemId).orElse(null);

        if(item.getBaskets().isEmpty()){
            item.setFavorites(new ArrayList<>(List.of(favorite)));
        }else{
            if(item.getFavorites().contains(favorite)){
                return;
            }
            item.getFavorites().add(favorite);
        }

        if(favorite.getItems().isEmpty()){
            favorite.setItems(new ArrayList<>(List.of(item)));
        }else{
            favorite.getItems().add(item);
        }

        favoriteRepository.save(favorite);
        itemRepository.save(item);
    }
}
