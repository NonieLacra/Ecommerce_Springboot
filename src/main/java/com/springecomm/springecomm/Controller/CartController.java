package com.springecomm.springecomm.Controller;

import com.springecomm.springecomm.Entity.CartItem;
import com.springecomm.springecomm.Entity.Product;
import com.springecomm.springecomm.Entity.User;
import com.springecomm.springecomm.Repository.CartRepository;
import com.springecomm.springecomm.Repository.ProductRepository;
import com.springecomm.springecomm.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller

public class CartController {

    @Autowired
    private  final CartRepository cartItemRepository;
    private final UserRepository userRepository;

    private  final ProductRepository productRepository;
    public CartController(CartRepository cartItemRepository, UserRepository userRepository, ProductRepository productRepository){
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository= productRepository;
    }

    // Show cart
    @GetMapping("/cart")
    public String showCart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        // Fetch the user's cart items
            List<CartItem> cartItems = cartItemRepository.findByUser(user);

        // Add the cart items to the model
        model.addAttribute("cartItems", cartItems);

        return "cart";
    }

    // Add item to cart
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        // Fetch the product
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            // Handle product not found
            return "redirect:/buyer_product_catalog";
        }
        Product product = optionalProduct.get();

        // Check if the item already exists in the user's cart
        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingCartItem != null) {
            // Increment the quantity if the item already exists
            existingCartItem.setQuantity(existingCartItem.getQuantity() + 1);
            cartItemRepository.save(existingCartItem);
        } else {
            // Create a new cart item with quantity 1
            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setUser(user);
            newCartItem.setQuantity(1);
            cartItemRepository.save(newCartItem);
        }

        return "redirect:/buyer_product_catalog";
    }

    @GetMapping("/cart/remove/{cartItemId}")
    public String removeFromCart(@PathVariable("cartItemId") Long cartItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        // Fetch the cart item
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            // Handle cart item not found
            return "redirect:/cart";
        }
        CartItem cartItem = optionalCartItem.get();

        // Check if the cart item belongs to the user
        if (!cartItem.getUser().equals(user)) {
            // Handle unauthorized access to cart item
            return "redirect:/cart";
        }

        // Remove the cart item
        cartItemRepository.delete(cartItem);

        return "redirect:/cart";
    }

    // Update cart item quantity
    @PostMapping("/cart/update")
    public String updateCartItemQuantity(@RequestParam("cartItemId") Long cartItemId, @RequestParam("quantity") int quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName());

        // Fetch the cart item
        Optional<CartItem> optionalCartItem = cartItemRepository.findById(cartItemId);
        if (optionalCartItem.isEmpty()) {
            // Handle cart item not found
            return "redirect:/cart";
        }
        CartItem cartItem = optionalCartItem.get();

        // Check if the cart item belongs to the user
        if (!cartItem.getUser().equals(user)) {
            // Handle unauthorized access to cart item
            return "redirect:/cart";
        }

        // Update the cart item quantity
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return "redirect:/cart";
    }


}
