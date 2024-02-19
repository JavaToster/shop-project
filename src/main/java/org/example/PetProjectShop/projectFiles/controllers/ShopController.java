package org.example.PetProjectShop.projectFiles.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.PetProjectShop.projectFiles.models.Category;
import org.example.PetProjectShop.projectFiles.models.Comment;
import org.example.PetProjectShop.projectFiles.models.Item;
import org.example.PetProjectShop.projectFiles.models.Shop;
import org.example.PetProjectShop.projectFiles.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/shop")
public class ShopController  {
    private ItemService itemService;
    private CategoryService categoryService;
    private ShopService shopService;
    private CommentService commentService;
    private PersonService personService;
    private ChatService chatService;

    @Autowired
    public void setItemService(ItemService itemService) {
        this.itemService = itemService;
    }

    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    public void setShopService(ShopService shopService) {
        this.shopService = shopService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Autowired
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping({"", "/"})
    public String mainWindow(Model model, Principal principal, HttpServletResponse response) {
        model.addAttribute("items", itemService.findAllItems());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("category", new Category());
        model.addAttribute("item", new Item());

        personService.deleteCookies(response);
        personService.addCookieId(response, principal.getName());

        return "/html/main";
    }

    @GetMapping({"/{id}/add-item", "/{id}/add-item/"})
    public String addItem(Model model, @PathVariable("id") int id) {
        model.addAttribute("shop_id", id);
        model.addAttribute("item", new Item());
        return "/html/item/newItem";
    }

    @PostMapping({"/{id}/add-item-step-1", "/{id}/add-item-step-1/" })
    public String addItemPostStepOne(@PathVariable("id") int id, @ModelAttribute("item") @Valid  Item item, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            return "/html/item/newItem";
        }

        item.setId(0);

        int indexItemInCash = itemService.addToCashStep1(item);
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("item_id", indexItemInCash);
        model.addAttribute("shop_id", id);


        return "/html/item/newItemStep2";
    }

    @PostMapping({"/{id}/add-item-step-2", "/{id}/add-item-step-2/"})
    public String addItemPostStepTwo(@ModelAttribute("category") Category category, Model model,
                                     @RequestParam("item-id") int itemId, @PathVariable("id") int shopId) {
        itemService.addToCashStep2(itemId, categoryService.findById(category.getId()));
        model.addAttribute("shopId", shopId);
        model.addAttribute("itemId", itemId);
        shopService.addCategory(shopService.findById(shopId), categoryService.findById(category.getId()));

        return "/html/item/newItemStep3";
    }

    @PostMapping({"/{id}/add-item-step-3", "/{id}/add-item-step-3"})
    public String addItemStepThree(@RequestParam("image") MultipartFile image, @RequestParam("item-id") int itemId, @PathVariable("id") int shopId){
        itemService.addCToCashStep3(itemId, image);

        itemService.add(shopId, itemId);

        return "redirect:/shop/";
    }

    @GetMapping({"/{id}", "/{id}/"})
    public String showShop(@PathVariable("id") int id, Model model, @CookieValue("username") String username) {
        model.addAttribute("categories", categoryService.findCategoriesByShopId(id));
        model.addAttribute("items", itemService.findItemByShop(id));
        model.addAttribute("category", new Category());
        model.addAttribute("shop", shopService.findById(id));
        model.addAttribute("isOwner", personService.isOwner(shopService.findById(id).getPerson().getUsername(), username));
        model.addAttribute("userUsername", username);

        return "/html/shop/shop";
    }

    @PostMapping({"/redirect-to-categories", "/redirect-to-categories"})
    public String redirectToCategories(@ModelAttribute("category") Category category) {
        return "redirect:/shop/category/" + category.getId()+"/";
    }

    @GetMapping({"/category/{categoryId}", "/category/{categoryId}/"})
    public String showCategory(@PathVariable("categoryId") int categoryId, Model model) {
        model.addAttribute("items", itemService.findByCategory(categoryId));
        model.addAttribute("item", new Item());
        model.addAttribute("category", categoryService.findById(categoryId));

        return "/html/category/showCategory";
    }

    @PostMapping({"/search", "/search/"})
    public String search(@RequestParam("search-text") String searchText, Model model) {
        model.addAttribute("items", itemService.findItemLikeSearchText(searchText));
        model.addAttribute("item", new Item());
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("text", searchText);


        return "/html/search";
    }

    @GetMapping({"/item/{id}", "/item/{id}/"})
    public String showItem(@PathVariable("id") int id, Model model) {
        Item item = itemService.findById(id);

        model.addAttribute("item", item);
        model.addAttribute("comments", commentService.findByItem(id));
        model.addAttribute("comment", new Comment());
        model.addAttribute("date", item.getDate());

        return "/html/item/showItem";
    }

    @GetMapping({"/item/{id}/edit", "/item/{id}/edit/"})
    public String editItem(@PathVariable("id") int id, Model model){
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        model.addAttribute("itemId", item.getId());
        model.addAttribute("shop", item.getShop());

        return "/html/item/editItem";
    }

    @PostMapping({"/item/{id}/edit", "/item/{id}/edit/"})
    public String editItemPost(@PathVariable("id") int id, @ModelAttribute("item") Item item){
        itemService.edit(id, item);

        return "redirect:/shop/item/"+id;
    }

    @GetMapping({"/{shopId}/category/{categoryId}", "/{shopId}/category/{categoryId}/"})
    public String showCategoryOfShop(@PathVariable("shopId") int shopId, @PathVariable("categoryId") int categoryId,
                                     Model model){
        model.addAttribute("items", itemService.findByShopAndCategory(shopId, categoryId));
        model.addAttribute("item", new Item());
        model.addAttribute("category", categoryService.findById(categoryId));

        return "/html/category/showCategory";
    }

    @PostMapping({"/item/{itemId}/add-comment", "/item/{itemId}/add-comment/"})
    public String addComment(@PathVariable("itemId") int itemId, @ModelAttribute("comment") Comment comment) {
        if(comment.getText().isEmpty()){
            return "redirect:/shop/item/"+itemId+"/";
        }

        commentService.addComment(itemId, comment);

        return "redirect:/shop/item/" + itemId;
    }

    @PostMapping("/{shopId}/create-new-chat")
    public String createNewChat(@PathVariable("shopId") int shopId, @RequestParam("username") String username) throws InterruptedException {
        chatService.createNewChat(shopId, username);

        Thread.sleep(1000);

        return "redirect:/chat/"+chatService.getLastChatId()+"/";
    }
}
