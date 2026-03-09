package dev.magadiflo.hexagonal.app.posts.infrastructure.inbound.controllers;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import dev.magadiflo.hexagonal.app.posts.domain.service.PostCreateUseCasePort;
import dev.magadiflo.hexagonal.app.posts.domain.service.PostFindUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/posts")
public class PostController {

    private final PostFindUseCasePort postFindUseCasePort;
    private final PostCreateUseCasePort postCreateUseCasePort;

    @GetMapping
    public ResponseEntity<List<PostQuery>> findAllPosts() {
        return ResponseEntity.ok(this.postFindUseCasePort.findAllPosts());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<PostQuery> findPostById(@PathVariable Long id) {
        return ResponseEntity.ok(this.postFindUseCasePort.findPostById(id));
    }

    @GetMapping(path = "/user/{userId}")
    public ResponseEntity<List<PostQuery>> findAllPostsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(this.postFindUseCasePort.findAllPostsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<PostQuery> savePosts(@RequestBody PostCommand postCommand) {
        PostQuery postDB = this.postCreateUseCasePort.createPost(postCommand);
        URI uri = URI.create("/api/v1/posts/" + postDB.getId());
        return ResponseEntity.created(uri).body(postDB);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<PostQuery> updatePost(@RequestBody PostCommand postCommand, @PathVariable Long id) {
        return ResponseEntity.ok(this.postCreateUseCasePort.updatePost(postCommand, id));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        this.postCreateUseCasePort.deletePost(id);
        return ResponseEntity.noContent().build();
    }

}
