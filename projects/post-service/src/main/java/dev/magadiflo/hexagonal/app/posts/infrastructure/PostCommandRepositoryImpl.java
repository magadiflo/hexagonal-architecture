package dev.magadiflo.hexagonal.app.posts.infrastructure;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import dev.magadiflo.hexagonal.app.posts.domain.repository.PostCommandRepository;
import dev.magadiflo.hexagonal.app.posts.infrastructure.outbound.external.JsonPlaceholderAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostCommandRepositoryImpl implements PostCommandRepository {

    private final JsonPlaceholderAPIClient jsonPlaceholderAPIClient;

    @Override
    public Optional<PostQuery> createPost(PostCommand postCommand) {
        return Optional.ofNullable(this.jsonPlaceholderAPIClient.createPost(postCommand));
    }

    @Override
    public Optional<PostQuery> updatePost(PostCommand postCommand, Long postId) {
        return Optional.ofNullable(this.jsonPlaceholderAPIClient.updatePost(postCommand, postId));
    }

    @Override
    public void deletePost(Long id) {
        this.jsonPlaceholderAPIClient.deletePost(id);
    }
}
