package org.example.PetProjectShop.projectFiles.services;

import org.example.PetProjectShop.projectFiles.repositories.*;
import org.example.PetProjectShop.projectFiles.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {
    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private CategoryRepository categoryRepository;
    private BasketRepository basketRepository;
    //for adding item settings in 3 steps
    //step 1: add item name, description and price
    //step 2: add item category
    //step 3: add item image
    private List<Item> itemsCash = new ArrayList<>();
    private JdbcTemplate jdbcTemplate;
    private FavoriteRepository favoriteRepository;

    @Autowired
    public void setFavoriteRepository(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Autowired
    public void setBasketRepository(BasketRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Autowired
    public void setItemRepository(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Autowired
    public void setShopRepository(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Item> findAllItems(){
        return itemRepository.findAll();
    }

    @Transactional
    public void add(int shopId, int itemId){
        //find shop by id
        Shop shop = shopRepository.findById(shopId).orElse(null);

        //get item from cash, item is ready to add to database
        Item item = itemsCash.get(itemId);
        //set shop
        item.setShop(shop);
        //set item to items list of shop
        shop.getItems().add(item);
        //set adding date
        item.setDate(Date.valueOf(LocalDate.now()).getTime());
        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findItemByShop(int id){
        return itemRepository.findByShop(shopRepository.findById(id).orElse(null));
    }

    //add item to cash with item name, description and price
    public int addToCashStep1(Item item){

        itemsCash.add(item);
        return itemsCash.indexOf(item);
    }

    //update item in cash(add category)
    public void addToCashStep2(int id, Category category){

        //get item from cash
        Item item = itemsCash.get(id);
        //next set category to this item
        item.setCategory(category);

        //update item in cash
        itemsCash.set(id, item);
    }

    //update item in cash(add image(image path))
    public void addCToCashStep3(int id, MultipartFile file){
        String path = "C:/Java/PetProjectShop/src/main/resources/static/images/item/";
        String fileName = file.getOriginalFilename();
        //split original file name by .
        int dotIndex = fileName.lastIndexOf(".");
        //set new name -> last item id+1.expansion (1.jpg, 2.jpeg)
        String imageName = (getLastImageId()+1)+fileName.substring(dotIndex);

        try{
            file.transferTo(new File(path+imageName));
        }catch (IOException e){
            e.printStackTrace();
        }

        Item item = itemsCash.get(id);
        item.setImageName(imageName);
        itemsCash.set(id, item);
    }

    @Transactional(readOnly = true)
    public List<Item> findByCategory(int id){
        return itemRepository.findByCategory(categoryRepository.findById(id).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<Item> findItemLikeSearchText(String text){
        return itemRepository.findByItemNameLike("%"+text+"%");
    }

    @Transactional(readOnly = true)
    public Item findById(int id){
        return itemRepository.findById(id).orElse(null);
    }

    @Transactional
    public void edit(int id, Item updatedItem){
        Item item = itemRepository.findById(id).orElse(null);
        item.setItemName(updatedItem.getItemName());
        item.setItemDescription(updatedItem.getItemDescription());
        item.setPrice(updatedItem.getPrice());

        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findByShopAndCategory(int shopId, int categoryId) {
        return itemRepository.findByShopAndCategory(shopRepository.findById(shopId).orElse(null), categoryRepository.findById(categoryId).orElse(null));
    }

    //for get last item id in database -> set id in image id (adding item to cash step 3)
    private int getLastImageId(){
        return jdbcTemplate.query("SELECT id FROM Item ORDER BY id DESC", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        }).stream().findAny().orElse(0);
    }

    @Transactional(readOnly = true)
    public List<Item> findItemsByBasketId(int basketId){
        return itemRepository.findByBaskets(basketRepository.findById(basketId).orElse(null));
    }

    @Transactional(readOnly = true)
    public List<Item> findItemsByFavoriteId(int favoriteId){
        return itemRepository.findByFavorites(favoriteRepository.findById(favoriteId).orElse(null));
    }
}
