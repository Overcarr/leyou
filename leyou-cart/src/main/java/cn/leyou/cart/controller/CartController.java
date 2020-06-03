package cn.leyou.cart.controller;


import cn.leyou.cart.pojo.Cart;
import cn.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("save")
    public ResponseEntity<Void> saveCart(@RequestBody Cart cart){
        cartService.saveCare(cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("selectAll")
    public ResponseEntity<List<Cart>> selectAll(){
        List<Cart> carts = cartService.selectAll();
        if (carts==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(carts);
    }

    @PostMapping("update")
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateNum(cart);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> delete(@PathVariable String skuId){
        this.cartService.delect(skuId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("saveLocal")
    public ResponseEntity<Void> saveLocal(@RequestBody List<Cart> carts){
        this.cartService.saveLocal(carts);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
