package com.example.service;

import com.example.entity.User;

public interface UserService {
    String sendMail(String toMail);

    User findByUsername(String username);

    Integer addUser(User user);

    Integer updatePasswordById(Integer uid,String newPassword);

    String sendRegisterMail(String toMail);

    boolean emailFormat(String email);
}
