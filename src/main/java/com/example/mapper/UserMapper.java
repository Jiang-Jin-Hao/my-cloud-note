package com.example.mapper;

import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    List<User> findAll();

    Integer addUser(User user);

    Integer updatePasswordById(@Param("uid")Integer uid,@Param("password") String password);
}
