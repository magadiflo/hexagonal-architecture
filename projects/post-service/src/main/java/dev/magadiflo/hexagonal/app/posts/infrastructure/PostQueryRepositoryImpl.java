package dev.magadiflo.hexagonal.app.posts.infrastructure;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import dev.magadiflo.hexagonal.app.posts.domain.repository.PostQueryRepository;
import dev.magadiflo.hexagonal.app.posts.infrastructure.outbound.external.JsonPlaceholderAPIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JsonPlaceholderAPIClient placeholderAPIClient;

    @Override
    public List<PostQuery> findAllPosts() {
        return this.placeholderAPIClient.getAllPosts();
    }

    @Override
    public Optional<PostQuery> findPostById(Long id) {
        return Optional.ofNullable(this.placeholderAPIClient.findPostById(id));
    }

    @Override
    public List<PostQuery> searchPostsByParams(Map<String, String> params) {
        return this.placeholderAPIClient.searchPostByParam(params);
    }
}
