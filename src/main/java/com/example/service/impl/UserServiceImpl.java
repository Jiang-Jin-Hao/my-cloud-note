package com.example.service.impl;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JavaMailSenderImpl javaMailSender;

    @Value("${rPassword.mail.title}")
    private String TITLE;
    @Value("${rPassword.mail.text}")
    private String TEXT;
    @Value("${register.mail.title}")
    private String REGISTER_TITLE;
    @Value("${register.mail.text}")
    private String REGISTER_TEXT;

    @Override
    public String sendMail(String toMail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(javaMailSender.getUsername());

        message.setTo(toMail);
        message.setSubject(TITLE);
        String registerCaptcha = getCaptcha(); // 验证码
        message.setText(TEXT.replaceAll("captcha", registerCaptcha));
        javaMailSender.send(message);

//        MimeMessage mimeMessage = new MimeMessage();


        return registerCaptcha;
    }

    @Override
    public String sendRegisterMail(String toMail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(javaMailSender.getUsername());
        message.setTo(toMail);
        message.setSubject(REGISTER_TITLE);
        String registerCaptcha = getCaptcha(); // 验证码
        message.setText(REGISTER_TEXT.replaceAll("captcha", registerCaptcha));
        javaMailSender.send(message);
        return registerCaptcha;
    }

    @Override
    public boolean emailFormat(String email) {
        return Pattern.compile("[a-zA-Z0-9]+[\\.]{0,1}[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]+").matcher(email).matches();
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Integer addUser(User user) {
        return userMapper.addUser(user);
    }

    @Override
    public Integer updatePasswordById(Integer uid, String newPassword) {
        String password = newPassword;
        return userMapper.updatePasswordById(uid, password);
    }

    private String getCaptcha() {
        // 随机的四位数验证码
        Random r = new Random(System.currentTimeMillis());
        String captcha = "";
        for (int i = 0; i < 4; i++) {
            int ran1 = r.nextInt(9);
            captcha += ran1;
        }

        return captcha;
    }
}
