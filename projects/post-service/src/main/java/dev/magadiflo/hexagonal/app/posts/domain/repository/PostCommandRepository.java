package dev.magadiflo.hexagonal.app.posts.domain.repository;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;

import java.util.Optional;

public interface PostCommandRepository {
    Optional<PostQuery> createPost(PostCommand postCommand);

    Optional<PostQuery> updatePost(PostCommand postCommand, Long postId);

    void deletePost(Long id);
}
