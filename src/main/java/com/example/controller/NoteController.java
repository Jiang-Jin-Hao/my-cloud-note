package com.example.controller;

import com.example.entity.Note;
import com.example.entity.User;
import com.example.mapper.NoteMapper;
import com.example.vo.NoteQuery;
import com.example.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/note")
public class NoteController {

    @Autowired
    NoteMapper noteMapper;

    // 跳转到主页
    @GetMapping({"/index"})
    public String index(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:http://localhost/user/login.html";
        } else {
            return "/note/index.html";
        }

        // return "/note/index.html";
    }

    // 刷新笔记列表
    @PostMapping("/getTableList")
    @ResponseBody
    public Object getTableList(HttpSession session, NoteQuery noteQuery) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        noteQuery.setUid(user.getUid());
        List<Note> notes = noteMapper.findAllByUid(noteQuery);
        Long count = Long.valueOf(noteMapper.countAllByUid(noteQuery));

        return Result.OK(notes, count);

    }

    // 删除笔记
    @DeleteMapping()
    @ResponseBody
    public Object delNote(Integer nid, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        // Integer nid=note.getNid();
        Integer uid = user.getUid();
        Integer rows = noteMapper.delByNid(nid, uid);
        if (rows != 1) {
            return Result.error();
        } else {
            return Result.OK();
        }
    }

    // 修改笔记
    @PutMapping()
    @ResponseBody
    public Result<Object> editNote(HttpSession session, Note note) {
        User user = (User) session.getAttribute("user");
        Integer uid = user.getUid();
        if (!uid.equals(note.getUid())) {
            return Result.error("您只能修改自己的笔记");
        }
        note.setModifiedTime(new Date());
        Integer rows = noteMapper.updateByNidAndUid(note);
        return Result.OK();
    }

    // 新建笔记
    @PostMapping
    @ResponseBody
    public Result<Object> createNote(HttpSession session, Note note) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("用户未登录");
        }
        String title=note.getTitle().trim();
        String text=note.getText().trim();
        if (title == null||"".equals(title)) {
            return Result.error("标题不能为空，随便写点什么吧！");
        }else if (text == null||"".equals(text)){
            return Result.error("内容不能为空，随便写点什么吧！");
        }

        note.setUid(user.getUid());
        note.setTitle(title);
        note.setText(text);
        note.setCreateTime(new Date());
        note.setModifiedTime(new Date());
        Integer rows = noteMapper.insertNote(note);
        if (rows!=1){
            return Result.error();
        }else {
            return Result.OK();
        }
    }
}
