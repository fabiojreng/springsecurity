package junior.java.springsecurity.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import java.util.UUID;

@RestController
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDTO> feed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        var pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp");

        var postsPage = postRepository.findAll(pageRequest)
                .map(post -> new FeedItemDTO(
                        post.getPostId(),
                        post.getContent(),
                        post.getUser().getUsername()));

        var response = new FeedDTO(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDTO dto,
            JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var post = new Post();
        post.setUser(user.get());
        post.setContent(dto.content());

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
            JwtAuthenticationToken token) {

        var user = userRepository.findById(UUID.fromString(token.getName()));
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || post.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            postRepository.deleteById(postId);

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }
}