package com.springecomm.springecomm.Controller;


import com.springecomm.springecomm.Entity.Product;
import com.springecomm.springecomm.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private  ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/catalog")
    public String viewSellerCatalog(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "seller_product_catalog";
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add_product";
    }
    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") Product product){
        productRepository.save(product);
        return "redirect:/catalog";
    }
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        model.addAttribute("product", product);
        return "edit_product";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") Product updatedProduct) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        productRepository.save(product);
        return "redirect:/catalog";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct (@PathVariable ("id") Long id ) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        productRepository.delete(product);
        return "redirect:/catalog";
    }
}
