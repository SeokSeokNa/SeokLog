package com.seoklog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoklog.domain.Post;
import com.seoklog.repository.PostRepository;
import com.seoklog.request.PostCreate;
import com.seoklog.request.PostEdit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest //-->SpringBootTest 어노테이션이 있을경우 MockMvc 인젝션을 수행할 수 없음 , 그래서 SpringBootTest 어노테이션 사용시  AutoConfigureMockMvc 를 사용하여 인젝션 받아야함
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean() {
        postRepository.deleteAll();
    }

//    @Test
//    @DisplayName("/posts 요청시 Hello Wolrd를 출력!")
//    void test() throws Exception {
//        mockMvc.perform(get("/posts"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Hello World"))
//                .andDo(print());
//    }

    @Test
    @DisplayName("/posts 요청시 title값은 필수다.")
    void test2() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(jsonPath("$.validation.title").value("타이틀을 입력해주세요"))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {

        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        //객체를 json String 으로 변환
        String json = objectMapper.writeValueAsString(request);


        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andDo(print());

        //then
        assertEquals(1L, postRepository.count());


        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.", post.getTitle());
        assertEquals("내용입니다.", post.getContent());
    }


    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        //given
        Post post = Post.builder()
                .title("123456789012345")
                .content("bar")
                .build();

        postRepository.save(post);

        //expected
        mockMvc.perform(get("/posts/{postId}" , post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("1234567890"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andDo(print());
    }


    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj( i ->
                        Post.builder()
                                .title("seok 제목 " + i)
                                .content("seok 내용 " + i)
                                .build()

                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);


        //expected
        mockMvc.perform(get("/posts")
                        .param("page" , "1")
                        .param("size" , "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" , is(10)))
                .andExpect(jsonPath("$.[0].title").value("seok 제목 19"))
                .andExpect(jsonPath("$.[0].content").value("seok 내용 19"))
                .andDo(print());
    }

    @Test
    @DisplayName("페이지 0으로 요청하면 첫 페이지를 가져온다.")
    void test6() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0, 20)
                .mapToObj( i ->
                        Post.builder()
                                .title("seok 제목 " + i)
                                .content("seok 내용 " + i)
                                .build()

                )
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);


        //expected
        mockMvc.perform(get("/posts")
                        .param("page" , "0")
                        .param("size" , "10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()" , is(10)))
                .andExpect(jsonPath("$.[0].title").value("seok 제목 19"))
                .andExpect(jsonPath("$.[0].content").value("seok 내용 19"))
                .andDo(print());
    }


    @Test
    @DisplayName("글 제목 수정")
    void test7() throws Exception {
        //given
        Post post = Post.builder()
                .title("seok")
                .content("봉천동")
                .build();


        postRepository.save(post);


        PostEdit postEdit = PostEdit.builder()
                .title("seok2")
                .content("초가집")
                .build();


        //expected
        mockMvc.perform(patch("/posts/{postId}" , post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test8() throws Exception {
        Post post = Post.builder()
                .title("seok")
                .content("봉천동")
                .build();

        postRepository.save(post);

        mockMvc.perform(delete("/post/{postId}", post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("존재하지 않는 글 조회")
    void test9() throws Exception {

        //expected
        mockMvc.perform(get("/posts/{postId}" , 1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 - 존재하지 않는 글 조회")
    void test10() throws Exception {

        //given
        PostEdit postEdit = PostEdit.builder()
                .title("seok2")
                .content("초가집")
                .build();


        //expected
        mockMvc.perform(patch("/posts/{postId}" , 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("게시글 작성시 제목에는 '바보'가 포함 될 수 없다.")
    void test11() throws Exception {

        //given
        PostCreate request = PostCreate.builder()
                .title("바보")
                .content("내용입니다.")
                .build();

        //객체를 json String 으로 변환
        String json = objectMapper.writeValueAsString(request);


        //when
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

    }
}