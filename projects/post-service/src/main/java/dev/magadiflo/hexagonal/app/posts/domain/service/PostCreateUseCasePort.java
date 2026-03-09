package dev.magadiflo.hexagonal.app.posts.domain.service;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;

public interface PostCreateUseCasePort {
    PostQuery createPost(PostCommand postCommand);

    PostQuery updatePost(PostCommand postCommand, Long id);

    void deletePost(Long id);
}
