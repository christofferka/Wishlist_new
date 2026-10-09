package com.example.config;

import com.example.model.User;
import com.example.model.Wish;
import com.example.model.Wishlist;
import com.example.repository.UserRepository;
import com.example.repository.WishRepository;
import com.example.repository.WishlistRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final WishlistRepository wishlistRepo;
    private final WishRepository wishRepo;
    private final PasswordEncoder encoder;

    public DemoDataSeeder(UserRepository u, WishlistRepository wl, WishRepository w, PasswordEncoder e) {
        this.userRepo = u;
        this.wishlistRepo = wl;
        this.wishRepo = w;
        this.encoder = e;
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() > 0) return;

        User demo  = userRepo.save(new User("Demo",  "demo",               encoder.encode("demo")));
        User sofie = userRepo.save(new User("Sofie", "sofie@wishlist.dk",  encoder.encode("demo1234")));

        Wishlist nyfodt = saveList("Nyfødt baby", "11111111-1111-1111-1111-111111111111", demo);
        Wishlist fods   = saveList("Fødselsdag",  "22222222-2222-2222-2222-222222222222", demo);
        Wishlist bryl   = saveList("Bryllup",     "33333333-3333-3333-3333-333333333333", sofie);

        wish("BabyBjörn bæresele One",   "https://www.babybjorn.dk",         999.00, false, null,     nyfodt);
        wish("Mushie sutter 2-pak",      "https://mushie.com",               249.00, true,  "Mormor", nyfodt);
        wish("Liewood pusle-sæt",        "https://liewood.com",              349.00, false, null,     nyfodt);

        wish("Nike Air Force 1",         "https://www.nike.com/dk",         1099.00, false, null,     fods);
        wish("Fjällräven Kånken rygsæk", "https://www.fjallraven.com",       699.00, false, null,     fods);
        wish("LEGO Technic Lamborghini", "https://www.lego.com/da-dk",       899.00, false, null,     fods);

        wish("Royal Copenhagen krus-sæt","https://www.royalcopenhagen.com",  599.00, false, null,     bryl);
        wish("Zwilling knivsæt",         "https://www.zwilling.com",        2499.00, true,  "Søster", bryl);
        wish("Smeg brødrister",          "https://www.smeg.dk",             1799.00, false, null,     bryl);
    }

    private Wishlist saveList(String name, String shareId, User owner) {
        Wishlist w = new Wishlist(name, owner);
        w.setShareId(shareId);
        return wishlistRepo.save(w);
    }

    private void wish(String desc, String link, double price, boolean reserved, String reservedBy, Wishlist list) {
        Wish w = new Wish(desc, link, price, list);
        w.setReserved(reserved);
        w.setReservedBy(reservedBy);
        wishRepo.save(w);
    }
}
