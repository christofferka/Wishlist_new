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

import java.util.List;
// (RequestParam/PostMapping/PathVariable kommer fra wildcard-importen ovenfor)

/**
 * Controller: håndterer ønskesedler (flere pr. bruger).
 * Indeholder funktioner til visning, oprettelse, deling og sletning af ønskesedler.
 */
@Controller
@RequestMapping("/wishes")
public class WebWishlistController {

    private final WishlistService wishlistService;
    private final WishService wishService;

    public WebWishlistController(WishlistService wishlistService, WishService wishService) {
        this.wishlistService = wishlistService;
        this.wishService = wishService;
    }

    //  VIS ALLE ØNSKESEDLER
    @GetMapping("/lists")
    public String showAllWishlists(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        List<Wishlist> wishlists = wishlistService.getWishlistsByUser(currentUser);
        model.addAttribute("wishlists", wishlists);
        model.addAttribute("newWishlist", new Wishlist());
        return "wishlists"; // viser oversigten over ønskesedler
    }


    //  OPRET NY ØNSKESEDDEL
    @PostMapping("/createList")
    public String createNewWishlist(@ModelAttribute Wishlist newWishlist, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        wishlistService.createWishlist(newWishlist.getName(), currentUser);
        return "redirect:/wishes/lists";
    }

    //  VIS EN SPECIFIK ØNSKESEDDEL
    @GetMapping("/viewlist/{id}")
    public String viewSpecificWishlist(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        return wishlistService.getWishlistById(id)
                .filter(w -> w.getUser().getId().equals(currentUser.getId()))
                .map(wishlist -> {
                    List<Wish> wishes = wishService.getWishesByWishlist(wishlist);
                    model.addAttribute("wishes", wishes);
                    model.addAttribute("wishlist", wishlist);
                    model.addAttribute("shareId", wishlist.getShareId());
                    return "wishlist"; // viser ønskerne i én ønskeseddel
                })
                .orElse("redirect:/wishes/lists");
    }

    //  SLET ØNSKESEDDEL
    @PostMapping("/deletelist/{id}")
    public String deleteWishlist(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        wishlistService.getWishlistById(id).ifPresent(w -> {
            if (w.getUser().getId().equals(currentUser.getId())) {
                wishlistService.deleteWishlist(id);
            }
        });
        return "redirect:/wishes/lists";
    }

    //  DEL ØNSKESEDDEL (OFFENTLIG VISNING)
    @GetMapping("/share/{shareId}")
    public String viewSharedWishlist(@PathVariable String shareId, Model model) {
        return wishlistService.getWishlistByShareId(shareId)
                .map(wishlist -> {
                    List<Wish> wishes = wishService.getWishesByWishlist(wishlist);
                    model.addAttribute("wishlist", wishlist);
                    model.addAttribute("owner", wishlist.getUser().getUsername());
                    model.addAttribute("wishes", wishes);
                    return "sharedWishlist";
                })
                .orElse("wishlistNotFound");
    }

    //  RESERVER ØNSKE (gæst, via delingslink)
    @PostMapping("/share/{shareId}/reserve/{wishId}")
    public String reserveSharedWish(@PathVariable String shareId,
                                    @PathVariable Long wishId,
                                    @RequestParam String name) {
        return wishlistService.getWishlistByShareId(shareId)
                .map(w -> {
                    if (name != null && !name.isBlank()) {
                        wishService.reserveWish(wishId, name.trim());
                    }
                    return "redirect:/wishes/share/" + shareId;
                })
                .orElse("redirect:/");
    }

    //  OPHÆV RESERVATION (kun ejer af listen)
    @PostMapping("/share/{shareId}/cancel/{wishId}")
    public String cancelSharedReservation(@PathVariable String shareId,
                                          @PathVariable Long wishId,
                                          HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        return wishlistService.getWishlistByShareId(shareId)
                .map(w -> {
                    if (currentUser != null && w.getUser().getId().equals(currentUser.getId())) {
                        wishService.cancelReservation(wishId);
                    }
                    return "redirect:/wishes/share/" + shareId;
                })
                .orElse("redirect:/");
    }
}
