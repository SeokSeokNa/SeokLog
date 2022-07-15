package com.seoklog.repository;

import com.seoklog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>  , PostRepositoryCustom{
}
