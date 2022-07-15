package com.seoklog.repository;

import com.seoklog.domain.Post;
import com.seoklog.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);

}
