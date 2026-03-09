package dev.magadiflo.hexagonal.app.posts.application.create;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import dev.magadiflo.hexagonal.app.posts.domain.repository.PostCommandRepository;
import dev.magadiflo.hexagonal.app.posts.domain.service.PostCreateUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class PostCreateUseCase implements PostCreateUseCasePort {

    private final PostCommandRepository postCommandRepository;

    @Override
    public PostQuery createPost(PostCommand postCommand) {
        return this.postCommandRepository.createPost(postCommand)
                .orElseThrow(() -> new NoSuchElementException("No se retornó Post"));
    }

    @Override
    public PostQuery updatePost(PostCommand postCommand, Long id) {
        return this.postCommandRepository.updatePost(postCommand, id)
                .orElseThrow(() -> new NoSuchElementException("Ocurrió un error al actualizar"));
    }

    @Override
    public void deletePost(Long id) {
        this.postCommandRepository.deletePost(id);
    }
}
