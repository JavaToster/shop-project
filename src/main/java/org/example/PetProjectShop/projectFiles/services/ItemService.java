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
    private List<Item> itemsCash = new ArrayList<>();

    private JdbcTemplate jdbcTemplate;

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
        Shop shop = shopRepository.findById(shopId).orElse(null);

        Item item = itemsCash.get(itemId);
        item.setShop(shop);
        shop.getItems().add(item);
        item.setDate(Date.valueOf(LocalDate.now()).getTime());

        itemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findItemByShop(int id){
        return itemRepository.findByShop(shopRepository.findById(id).orElse(null));
    }

    public int addToCashStep1(Item item){
        itemsCash.add(item);
        return itemsCash.indexOf(item);
    }

    public void addToCashStep2(int id, Category category){
        Item item = itemsCash.get(id);
        item.setCategory(category);

        itemsCash.set(id, item);
    }

    public void addCToCashStep3(int id, MultipartFile file){
        String path = "C:/Java/PetProjectShop/src/main/resources/static/images/";
        String fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf(".");
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

    private int getLastImageId(){
        return jdbcTemplate.query("SELECT id FROM Item ORDER BY id DESC", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("id");
            }
        }).stream().findAny().orElse(0);
    }
}
