package dev.magadiflo.hexagonal.app.posts.application.find;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import dev.magadiflo.hexagonal.app.posts.domain.repository.PostQueryRepository;
import dev.magadiflo.hexagonal.app.posts.domain.service.PostFindUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class PostFindUseCase implements PostFindUseCasePort {

    private final PostQueryRepository postQueryRepository;

    @Override
    public List<PostQuery> findAllPosts() {
        return this.postQueryRepository.findAllPosts();
    }

    @Override
    public List<PostQuery> findAllPostsByUserId(Long userId) {
        return this.postQueryRepository.searchPostsByParams(Map.of("userId", String.valueOf(userId)));
    }

    @Override
    public PostQuery findPostById(Long id) {
        return this.postQueryRepository.findPostById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el post con id:" + id));
    }
}
