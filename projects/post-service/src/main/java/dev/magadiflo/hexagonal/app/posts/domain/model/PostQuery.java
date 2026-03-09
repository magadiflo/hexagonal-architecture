package dev.magadiflo.hexagonal.app.posts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PostQuery {
    private Long id;
    private Long userId;
    private String body;
    private String title;
}
