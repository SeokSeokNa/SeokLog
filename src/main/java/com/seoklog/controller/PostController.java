package com.seoklog.controller;

import com.seoklog.request.PostCreate;
import com.seoklog.request.PostEdit;
import com.seoklog.request.PostSearch;
import com.seoklog.response.PostResponse;
import com.seoklog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

//    @GetMapping("/posts")
//    public String get() {
//        return "Hello World";
//    }

    @PostMapping("/posts")
    public void post(@RequestBody @Valid PostCreate request) {
//        if (result.hasErrors()) {
//            List<FieldError> fieldErrors = result.getFieldErrors();
//            FieldError fieldError = fieldErrors.get(0);
//            String fieldName = fieldError.getField();
//            String errorMessage = fieldError.getDefaultMessage();
//
//            HashMap<String , String> error = new HashMap<>();
//            error.put(fieldName, errorMessage);
//            return error;
//        }
//      log.info("request={}" , request.toString());
        request.validate();

        postService.write(request);
    }

    /**
     * /posts -> 글 전체 조회 (검색 + 페이징)
     * /posts/{postId} -> 글 한개만 조회
     */
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        return postService.get(postId);
    }


    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        return postService.getList(postSearch);
    }

    @PatchMapping("/posts/{postId}")
    public PostResponse edit(@PathVariable Long postId, @RequestBody @Valid PostEdit postEdit) {
       return postService.edit(postId, postEdit);
    }

    @DeleteMapping("/post/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

}
