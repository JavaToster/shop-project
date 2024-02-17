package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.models.Basket;
import org.example.PetProjectShop.projectFiles.models.Item;
import org.example.PetProjectShop.projectFiles.repositories.BasketRepository;
import org.example.PetProjectShop.projectFiles.repositories.ItemRepository;
import org.example.PetProjectShop.projectFiles.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BasketService {
    private BasketRepository basketRepository;
    private PersonRepository personRepository;
    private ItemRepository itemRepository;

    @Autowired
    public void setBasketRepository(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
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
    public void save(Basket basket){
        basketRepository.save(basket);
    }

    public Basket findById(int id){
        return basketRepository.findById(id).orElse(null);
    }

    public Basket findBasketByPerson(int personId){
        return basketRepository.findByOwner(personRepository.findById(personId).orElse(null)).get();
    }

    @Transactional
    public void addItem(int personId, int itemId){
        Basket personBasket = basketRepository.findByOwner(personRepository.findById(personId).orElse(null)).get();
        Item item = itemRepository.findById(itemId).orElse(null);

        if(item.getBaskets().contains(personBasket)){
            return;
        }

        if(item.getBaskets().isEmpty()){
            item.setBaskets(new ArrayList<>(List.of(personBasket)));
        }else{
            if(item.getBaskets().contains(personBasket)){
                return;
            }
            item.getBaskets().add(personBasket);
        }

        if(personBasket.getItems().isEmpty()){
            personBasket.setItems(new ArrayList<>(List.of(item)));
        }else{
            personBasket.getItems().add(item);
        }

        basketRepository.save(personBasket);
        itemRepository.save(item);
    }
}
