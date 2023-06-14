package com.example.controller;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.example.vo.Result;
import com.wf.captcha.utils.CaptchaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    // 跳转到登录页面
    @RequestMapping("/login")
    public String login(HttpSession session) {
        return "/user/login.html";
    }

    // 登录按钮
    @PostMapping("/loginButton")
    @ResponseBody
    public Result<Object> login(User user, String captcha, HttpServletRequest request) {

        if (user.getUsername() == null || "".equals(user.getUsername())) {
            return Result.error("用户名不能为空");
        } else if (user.getPassword() == null || "".equals(user.getPassword())) {
            return Result.error("密码不能为空");
        } else if (captcha == null || "".equals(captcha)) {
            return Result.error("验证码不能为空");
        } else if (!CaptchaUtil.ver(captcha, request)) {
            CaptchaUtil.clear(request);  // 清除session中的验证码
            return Result.error("验证码错误！");
        }

        User dbUser = userMapper.findByUsername(user.getUsername());

        if (dbUser == null) {
            return Result.error("该用户不存在");
        } else if (!dbUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        // 用户信息存到session里
        HttpSession session = request.getSession();
        dbUser.setPassword(null);
        session.setAttribute("user", dbUser);

        return Result.OK();
    }

    // 跳转到注册页面
    @RequestMapping("/register")
    public String toRegister() {
        return "/user/register.html";
    }

    // 是否已登录
    @GetMapping("/isLogin")
    @ResponseBody
    public Result<Object> isLogin(HttpSession session) {
        Object user = session.getAttribute("user");
        // System.out.println("user="+user);
        if (user == null) {
            return Result.error();
        } else {
            return Result.OK();
        }
    }

    // 注册按钮
    @PostMapping("/register")
    @ResponseBody
    public Result<Object> register(User user, String captcha, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (user.getUsername() == null || "".equals(user.getUsername())) {
            return Result.error("用户名不能为空！");
        } else if (user.getPassword() == null || "".equals(user.getPassword())) {
            return Result.error("密码不能为空！");
        } else if (captcha == null || "".equals(captcha)) {
            return Result.error("验证码不能为空！");
        } else if (!CaptchaUtil.ver(captcha, request)) {
            CaptchaUtil.clear(request);  // 清除session中的验证码
            return Result.error("验证码错误！");
        }
        // 验证邮箱
        if (userService.emailFormat(user.getEmail())==false){
            return Result.error("邮箱格式错误");
        }
        // 校对上一次的发送邮箱的时间
        Long sendMailTime = (Long) session.getAttribute("sendMailTime");
        if (sendMailTime != null) {
            Long nowTime = System.currentTimeMillis();
            Long time = (nowTime - sendMailTime) / 1000; // 单位秒
            if (time < 60) {
                return Result.error("请" + (60 - time) + "秒后再重新获取验证码！");
            }
        }

        // 发送注册验证码
        String checkCaptcha = userService.sendRegisterMail(user.getEmail());
        // 记录QQ邮箱的验证码
        session.setAttribute("checkCaptcha", checkCaptcha);
        // 记录 QQ邮箱验证码验证 时间
        sendMailTime = System.currentTimeMillis();
        session.setAttribute("sendMailTime", sendMailTime);
        // 记录要注册的用户
        session.setAttribute("registerUser", user);
        return Result.OK();
    }

    // 注册 验证QQ验证码
    @GetMapping("/check/captcha/{captcha}")
    @ResponseBody
    public Result<Object> checkCaptcha(@PathVariable("captcha") String captcha, HttpSession session) {
        // 校对上一次的发送邮箱的时间
        Long sendMailTime = (Long) session.getAttribute("sendMailTime");
        if (sendMailTime != null) {
            Long nowTime = System.currentTimeMillis();
            Long time = (nowTime - sendMailTime) / 1000; // 单位秒
            if (time > 60) {
                return Result.error("验证码超时了！");
            }
        }
        // 判空
        String checkCaptcha = (String) session.getAttribute("checkCaptcha");
        if (captcha == null || "".equals(captcha)) {
            return Result.error("验证码不能为空");
        } else if (checkCaptcha == null || "".equals(checkCaptcha)) {
            return Result.error("验证码还未送达");
        } else if (!checkCaptcha.equals(captcha)) {
            return Result.error("验证码错误");
        }

        // 去注册
        User registerUser = (User) session.getAttribute("registerUser");
        User dbUser = userService.findByUsername(registerUser.getUsername());
        if (dbUser != null) {
            return Result.error("注册失败：用户已存在！");
        }
        Integer uid = userService.addUser(registerUser);
        // 用户信息存到session里
        registerUser.setPassword(null);
        session.setAttribute("user", registerUser);
        return Result.OK();
    }

    // 跳转到找回密码页面
    @RequestMapping("/forgot_password")
    public String toForgotPassword() {
        return "/user/forgot_password.html";
    }

    // 点击找回密码按钮
    @RequestMapping("/forgotPassword")
    @ResponseBody
    public Result<Object> toForgotPassword(User user, String captcha, HttpServletRequest request) {

        if (user.getUsername() == null || "".equals(user.getUsername())) {
            return Result.error("用户名不能为空");
        } else if (user.getEmail() == null || "".equals(user.getEmail())) {
            return Result.error("邮箱不能为空");
        } else if (captcha == null || "".equals(captcha)) {
            return Result.error("验证码不能为空");
        } else if (!CaptchaUtil.ver(captcha, request)) {
            CaptchaUtil.clear(request);  // 清除session中的验证码
            return Result.error("验证码错误！");
        }

        // 校对上一次的发送邮箱的时间
        HttpSession session = request.getSession();
        Long sendMailTime = (Long) session.getAttribute("sendMailTime");
        if (sendMailTime != null) {
            Long nowTime = System.currentTimeMillis();
            Long time = (nowTime - sendMailTime) / 1000; // 单位秒
            if (time < 60) {
                return Result.error("请" + (60 - time) + "秒后重试！");
            }
        }

        // 查询用户是否存在
        User dbUser = userService.findByUsername(user.getUsername());
        if (dbUser == null) {
            return Result.error("用户不存在！");
        } else if (!dbUser.getEmail().equals(user.getEmail())) {
            return Result.error("请输入自己的安全邮箱！");
        }
        // 发QQ验证码
        String checkCaptcha = userService.sendMail(user.getEmail());
        session.setAttribute("checkCaptcha", checkCaptcha);
        // 记录 QQ邮箱验证码验证 时间
        sendMailTime = System.currentTimeMillis();
        session.setAttribute("sendMailTime", sendMailTime);

        dbUser.setPassword(user.getPassword());

        session.setAttribute("newPasswordUser", dbUser);

        return Result.OK();
    }

    // 找回密码上传验证码
    @GetMapping("/check/captcha/newPassword/{captcha}")
    @ResponseBody
    public Result<Object> newPassword(@PathVariable("captcha") String captcha, HttpSession session) {
        // 校对上一次的发送邮箱的时间 是否验证码超时
        Long sendMailTime = (Long) session.getAttribute("sendMailTime");
        if (sendMailTime != null) {
            Long nowTime = System.currentTimeMillis();
            Long time = (nowTime - sendMailTime) / 1000; // 单位秒
            if (time > 60) {
                return Result.error("验证码超时了");
            }
        }

        // 验证码是否正确
        String checkCaptcha = (String) session.getAttribute("checkCaptcha");
        if (captcha == null || "".equals(captcha)) {
            return Result.error("验证码不能为空");
        } else if (checkCaptcha == null || "".equals(checkCaptcha)) {
            return Result.error("验证码还未送达");
        } else if (!checkCaptcha.equals(captcha)) {
            return Result.error("验证码错误");
        }

        // 修改密码 从缓存拿新密码
        User newPasswordUser = (User) session.getAttribute("newPasswordUser");
        Integer rows = userService.updatePasswordById(newPasswordUser.getUid(), newPasswordUser.getPassword());
        if (rows != 1) {
            return Result.error("修改密码出现未知错误！");
        } else {
            return Result.OK();
        }
    }

    // 拿到用户名
    @GetMapping("/getUsername")
    @ResponseBody
    public Result<Object> getUsername(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("用户未登录！");
        } else {
            String username = user.getUsername();
            return Result.OK(username, null);
        }

    }

    // 退出登录
    @GetMapping("/exit")
    @ResponseBody
    public Result<Object> exit(HttpSession session) {
        session.invalidate();
        return Result.OK();
    }
}
