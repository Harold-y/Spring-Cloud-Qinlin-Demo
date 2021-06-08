package org.chengbing.controller;

import org.apache.commons.codec.binary.StringUtils;
import org.chengbing.entity.User;
import org.chengbing.util.JwtUtils;
import org.chengbing.util.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @PostMapping("login")
    public Result login (User user) throws Exception {
        if (StringUtils.equals(user.getName(),"admin")&&StringUtils.equals(user.getPassword(),"admin")){
            System.out.println("用户==========="+user);
            //令牌
            String jwt = JwtUtils.createJWT(user);
            //
            return new Result(jwt);
        }
        return new Result(500,"输入的信息有错！！","错误");
    }
}
