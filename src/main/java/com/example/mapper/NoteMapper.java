package com.example.mapper;

import com.example.entity.Note;
import com.example.vo.NoteQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface NoteMapper {

    List<Note> findAllByUid(NoteQuery noteQuery);

    Integer countAllByUid(NoteQuery noteQuery);

    Integer delByNid(@Param("nid") Integer nid,@Param("uid") Integer uid);

    Integer updateByNidAndUid(Note note);

    Integer insertNote(Note note);
}
