package com.example.controller;

import com.example.model.User;
import com.example.model.Wish;
import com.example.model.Wishlist;
import com.example.service.WishService;
import com.example.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller: håndterer individuelle ønsker (tilføj, rediger, slet, reserver)
 */
@Controller
@RequestMapping("/wish")
public class WebWishController {

    private final WishService wishService;
    private final WishlistService wishlistService;

    public WebWishController(WishService wishService, WishlistService wishlistService) {
        this.wishService = wishService;
        this.wishlistService = wishlistService;
    }

    //  OPRET ØNSKE
    @GetMapping("/add/{wishlistId}")
    public String showAddForm(@PathVariable Long wishlistId, Model model, HttpSession session) {
        if (session.getAttribute("currentUser") == null) return "redirect:/login";
        model.addAttribute("wish", new Wish());
        model.addAttribute("wishlistId", wishlistId);
        return "form";
    }

    @PostMapping("/add/{wishlistId}")
    public String addWish(@PathVariable Long wishlistId, @ModelAttribute Wish wish, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Wishlist wishlist = wishlistService.getWishlistById(wishlistId).orElse(null);
        if (wishlist == null || !wishlist.getUser().getId().equals(currentUser.getId()))
            return "redirect:/wishes/lists";

        wish.setWishlist(wishlist);
        wishService.saveWish(wish);
        return "redirect:/wishes/viewlist/" + wishlistId;
    }

    //  REDIGER ØNSKE
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        return wishService.getWishById(id)
                .filter(w -> w.getWishlist().getUser().getId().equals(currentUser.getId()))
                .map(wish -> {
                    model.addAttribute("wish", wish);
                    return "edit";
                })
                .orElse("redirect:/wishes/lists");
    }

    @PostMapping("/update/{id}")
    public String updateWish(@PathVariable Long id, @ModelAttribute Wish updatedWish, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        return wishService.getWishById(id)
                .filter(w -> w.getWishlist().getUser().getId().equals(currentUser.getId()))
                .map(existing -> {
                    existing.setDescription(updatedWish.getDescription());
                    existing.setLink(updatedWish.getLink());
                    existing.setPrice(updatedWish.getPrice());
                    wishService.saveWish(existing);
                    return "redirect:/wishes/viewlist/" + existing.getWishlist().getId();
                })
                .orElse("redirect:/wishes/lists");
    }

    //  SLET ØNSKE
    @PostMapping("/delete/{id}")
    public String deleteWish(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        return wishService.getWishById(id)
                .map(wish -> {
                    Long wishlistId = wish.getWishlist().getId();
                    if (wish.getWishlist().getUser().getId().equals(currentUser.getId())) {
                        wishService.deleteWish(id);
                    }
                    return "redirect:/wishes/viewlist/" + wishlistId;
                })
                .orElse("redirect:/wishes/lists");
    }
}
