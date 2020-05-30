package cn.leyou.user.service;

import cn.leyou.user.dao.UserDao;
import cn.leyou.user.pojo.User;
import cn.leyou.utils.CodecUtils;
import cn.leyou.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static String KEY_PREFIX = "user:code:phone:";

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    //校验
    public Boolean check(String data, Integer type) {
        User user = new User();
        if (type==1){
            user.setUsername(data);
        }else if(type==2){
            user.setPhone(data);
        }

        return userDao.selectCount(user) == 0;
    }

    //发送验证码
    public Boolean sendCode(String phone) {
        String code = NumberUtils.generateCode(6);

        try {
            Map<String,String> map = new HashMap<>();
            map.put("phone",phone);
            map.put("code",code);
            this.rabbitTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code",map);
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            logger.error("发送短信失败。phone:{},code:{}",phone,code);
            return false;
        }

    }

    public Boolean register(User user,String code) {
        String s = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!s.equals(code)){
            return false;
        }
        String generateSalt = CodecUtils.generateSalt();
        user.setSalt(generateSalt);

        String md5Hex = CodecUtils.md5Hex(user.getPassword(), generateSalt);
        user.setPassword(md5Hex);
        user.setId(null);
        user.setCreated(new Date());
        Boolean b = this.userDao.insertSelective(user) ==1;

        if (b){
            this.stringRedisTemplate.delete(KEY_PREFIX+user.getPhone());
        }
        return b;
    }

    public User queryUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        User one = this.userDao.selectOne(user);
        if (one==null){
            return null;
        }
        if (!one.getPassword().equals(CodecUtils.md5Hex(password,one.getSalt()))){
            return null;
        }
        return one;
    }
}
