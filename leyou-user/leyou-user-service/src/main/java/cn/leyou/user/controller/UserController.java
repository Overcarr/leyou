package cn.leyou.user.controller;


import cn.leyou.user.pojo.User;
import cn.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable String data,@PathVariable Integer type){
        Boolean b = userService.check(data,type);
        if (b==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(b);
    }

    @PostMapping("send")
    public ResponseEntity<Boolean> sendCode(String phone){
        Boolean b = this.userService.sendCode(phone);
        if (b==null || !b){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(b);
    }

    @PostMapping("register")
    public ResponseEntity<Boolean> register(@Valid User user, @RequestParam("code")String code){
        Boolean b = this.userService.register(user,code);
        if (b==null || !b){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(b);
    }

    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,
                                          @RequestParam("password")String password){
        User user = this.userService.queryUser(username,password);
        if (user==null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(user);
    }

}
