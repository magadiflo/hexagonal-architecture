package dev.magadiflo.hexagonal.app.posts.domain.service;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;

import java.util.List;

public interface PostFindUseCasePort {
    List<PostQuery> findAllPosts();

    List<PostQuery> findAllPostsByUserId(Long userId);

    PostQuery findPostById(Long id);

}
