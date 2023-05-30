package com.springecomm.springecomm.Controller;

import com.springecomm.springecomm.Entity.Product;
import com.springecomm.springecomm.Entity.User;
import com.springecomm.springecomm.Repository.ProductRepository;
import com.springecomm.springecomm.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ThymeleafController {

    private final UserRepository userRepository;

    @Autowired

    private final PasswordEncoder passwordEncoder;
    private ProductRepository productRepository;

    public ThymeleafController(UserRepository userRepository, PasswordEncoder passwordEncoder, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;

    }
 // get reg


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        User newUser = new User();
        model.addAttribute("newuser", newUser);
        return "register";
    }

    // Process registration form submission
    // Process registration form submission
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("newuser") User user, @RequestParam("role") String role) {
        // Check if the username already exists
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "redirect:/register?error=Username already exists";
        }

        // Set the user's role based on the selected option
        if ("seller".equals(role)) {
            user.setRole("SELLER");
        } else if ("buyer".equals(role)) {
            user.setRole("BUYER");
        }

        // Encrypt the password before saving to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        userRepository.save(user);

        // Redirect the user to the login page
        return "redirect:/loginUser.html";
    }


    // Show login page
    @GetMapping("/loginUser.html")
    public String showLoginPage() {
        return "loginUser";
    }

    // Process login form submission
    @PostMapping("/loginUser")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // If login is successful, redirect to another HTML file
            if ("SELLER".equals(user.getRole())) {
                return "redirect:/seller_product_catalog.html";
            } else {
                return "redirect:/buyer_product_catalog.html";
            }
        } else {
            // If login fails, you can redirect back to the login page or display an error message
            return "redirect:/loginUser.html?error=Invalid credentials";
        }
    }

    // Show seller product catalog
    @GetMapping("/seller/catalog")
    public String showSellerProductPage() {
        return "seller_product_catalog";
    }


    // Show buyer home page
    @GetMapping("/buyer_product_catalog")
    public String showBuyerHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        // Fetch all products from the repository
        List<Product> products = productRepository.findAll();

        // Add the products to the model
        model.addAttribute("products", products);

        // Add the user's cart items to the model
        model.addAttribute("cartItems", user.getCartItems());

        return "buyer_product_catalog";
    }




}
