package org.chengbing.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.chengbing.entity.User;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Harold澂冰
 * @Date: 2021/5/31 16:51
 */
public class JwtUtils {
    //密钥 -- 根据实际项目，这里可以做成配置
    public static final String KEY = "ifyoucanhearthis,youarealone";
    /**
     * 由字符串生成加密key
     * @return
     */
    public static SecretKey generalKey(){
        byte[] encodedKey = Base64.decodeBase64(KEY);
        SecretKeySpec key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
    /**
     * 生成token
     * @param
     * @return
     * @throws Exception
     */
    public static String createJWT(User user) throws Exception {
        // 指定签名的时候使用的签名算法，也就是header那部分，jjwt已经将这部分内容封装好了。
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 生成JWT的时间
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //30分钟 以后的时间
        long ttmillions=30*60*1000;//30分钟
        long expMillis = nowMillis + ttmillions; //token过期的时候
        Date exp = new Date(expMillis);//失效的时间
        // 创建payload的私有声明（根据特定的业务需要添加，如果要拿这个做验证，一般是需要和jwt的接收方提前沟通好验证方式的）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",user.getId());
        claims.put("name", user.getName());
        claims.put("password", user.getPassword());
        SecretKey key = generalKey();

        // 下面就是在为payload添加各种标准声明和私有声明了
        JwtBuilder builder = Jwts.builder() // 这里其实就是new一个JwtBuilder，设置jwt的body
                .setClaims(claims)          // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                // 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setIssuedAt(now)           // iat: jwt的签发时间
                .setIssuer("admin")
                .setExpiration(exp)         //过期的时间
                // issuer：jwt签发人
                //.setSubject(subject)        // sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .signWith(signatureAlgorithm, key); // 设置签名使用的签名算法和签名使用的秘钥

        return builder.compact();
    }
    /**
     * 解密jwt
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey key = generalKey();  //签名秘钥，和生成的签名的秘钥一模一样
        Claims claims = Jwts.parser()  //得到DefaultJwtParser
                .setSigningKey(key)                 //设置签名的秘钥
                .parseClaimsJws(jwt).getBody();     //设置需要解析的jwt
        return claims;
    }
    /**
     * 检查token
     * @return
     */
    public static Claims checkToken(String jwtToken) throws Exception {

        Claims claims = JwtUtils.parseJWT(jwtToken);
        String subject = claims.getSubject();
//          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//          Date expiration = claims.getExpiration();
        return claims;
    }


    public static void main(String[] args) throws Exception {
        User user=new User(1,"admin","admin");
        String jwt = JwtUtils.createJWT(user);
        System.out.println(jwt);
        Claims claims = JwtUtils.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6ImFkbWluIiwibmFtZSI6ImFkbWluIiwiaXNzIjoiYWRtaW4iLCJpZCI6MSwiZXhwIjoxNjIyNDI1NzE1LCJpYXQiOjE2MjI0MjU3MTR9.yyJ_-bZdFZoLQcoETi31iohPl0bY3buB3EUyYmTaq2A");
        System.out.println(claims);
    }
}
