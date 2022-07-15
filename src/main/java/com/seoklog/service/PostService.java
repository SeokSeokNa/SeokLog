package com.seoklog.service;

import com.seoklog.domain.Post;
import com.seoklog.domain.PostEditor;
import com.seoklog.exception.PostNotFound;
import com.seoklog.repository.PostRepository;
import com.seoklog.request.PostCreate;
import com.seoklog.request.PostEdit;
import com.seoklog.request.PostSearch;
import com.seoklog.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class  PostService {


    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }

    public PostResponse get(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFound::new);

        return   PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();


    }

    public List<PostResponse> getList(PostSearch postSearch) {
        //Pageable pageable = PageRequest.of(page, 5 , Sort.by(Sort.Direction.DESC,"id"));

        return postRepository.getList(postSearch).stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse edit(Long id , PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        PostEditor.PostEditorBuilder postEditorBuilder = post.toEditor();

        if (postEdit.getTitle() != null) {
            postEditorBuilder.title(postEdit.getTitle());
        }

        if (postEdit.getContent() != null) {
            postEditorBuilder.content(postEdit.getContent());
        }


        post.edit(postEditorBuilder.build());

        return new PostResponse(post);
    }

    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFound::new );

        postRepository.delete(post);
    }
}
