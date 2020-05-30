package cn.leyou.user.dao;

import cn.leyou.user.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UserDao extends Mapper<User> {
}
