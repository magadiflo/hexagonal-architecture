package dev.magadiflo.hexagonal.app.posts.infrastructure.outbound.external;

import dev.magadiflo.hexagonal.app.posts.domain.model.PostCommand;
import dev.magadiflo.hexagonal.app.posts.domain.model.PostQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "jsonplaceholder", url = "https://jsonplaceholder.typicode.com", path = "/posts")
public interface JsonPlaceholderAPIClient {
    @GetMapping
    List<PostQuery> getAllPosts();

    @GetMapping
    List<PostQuery> searchPostByParam(@RequestParam Map<String, String> params);

    @GetMapping(path = "/{id}")
    PostQuery findPostById(@PathVariable Long id);

    @PostMapping
    PostQuery createPost(@RequestBody PostCommand postCommand);

    @PutMapping(path = "/{id}")
    PostQuery updatePost(@RequestBody PostCommand postCommand, @PathVariable Long id);

    @DeleteMapping(path = "/{id}")
    void deletePost(@PathVariable Long id);
}
