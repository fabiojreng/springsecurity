package junior.java.springsecurity.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import junior.java.springsecurity.controller.dto.CreatePostDTO;
import junior.java.springsecurity.controller.dto.FeedDTO;
import junior.java.springsecurity.controller.dto.FeedItemDTO;
import junior.java.springsecurity.models.Post;
import junior.java.springsecurity.models.Role;
import junior.java.springsecurity.repositories.PostRepository;
import junior.java.springsecurity.repositories.UserRepository;
import junior.java.springsecurity.services.PostService;

import java.util.UUID;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> newPost(@RequestBody CreatePostDTO createPostDto, @AuthenticationPrincipal Jwt jwt) {
        postService.createPost(createPostDto, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDTO> feed(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var feed = postService.listPosts(page, pageSize);
        return ResponseEntity.ok(feed);
    }

    @DeleteMapping("post/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
            JwtAuthenticationToken token) {

        var userId = UUID.fromString(token.getName());
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }
}