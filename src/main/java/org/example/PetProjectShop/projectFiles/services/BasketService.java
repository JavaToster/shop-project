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

    //for adding new basket
    @Transactional
    public void save(Basket basket){
        basketRepository.save(basket);
    }

    //for find basket by him id
    public Basket findById(int id){
        return basketRepository.findById(id).orElse(null);
    }

    //for find basket by him owner
    public Basket findBasketByPerson(int personId){
        return basketRepository.findByOwner(personRepository.findById(personId).orElse(null)).get();
    }

    //for add item to basket
    @Transactional
    public void addItem(int personId, int itemId){
        //getting basket, next get item by id, eventually add item to basket
        Basket personBasket = basketRepository.findByOwner(personRepository.findById(personId).orElse(null)).get();
        Item item = itemRepository.findById(itemId).orElse(null);

        //Если в корзине уже есть такой товар
        if(item.getBaskets().contains(personBasket)){
            return;
        }

        //Если у товара не инициализирован список корзин то мы его инициализируем уже с одной корзиной внутри
        if(item.getBaskets().isEmpty()){
            item.setBaskets(new ArrayList<>(List.of(personBasket)));
        }else{
            //Опять проверяем нету ли такого товара в корзине
            if(item.getBaskets().contains(personBasket)){
                return;
            }
            item.getBaskets().add(personBasket);
        }
        //Тоже самое как с проверкой на инициализацию корзин у товара
        if(personBasket.getItems().isEmpty()){
            personBasket.setItems(new ArrayList<>(List.of(item)));
        }else{
            personBasket.getItems().add(item);
        }

        basketRepository.save(personBasket);
        itemRepository.save(item);
    }
}
