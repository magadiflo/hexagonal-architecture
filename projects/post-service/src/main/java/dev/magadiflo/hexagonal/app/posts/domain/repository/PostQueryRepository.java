package dev.magadiflo.hexagonal.app.posts.domain.repository;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostQueryRepository {
    List<PostQuery> findAllPosts();

    Optional<PostQuery> findPostById(Long id);

    List<PostQuery> searchPostsByParams(Map<String, String> params);
}
