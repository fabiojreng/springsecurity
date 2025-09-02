package junior.java.springsecurity.services;

import junior.java.springsecurity.controller.dto.CreatePostDTO;
import junior.java.springsecurity.controller.dto.FeedDTO;
import junior.java.springsecurity.controller.dto.FeedItemDTO;
import junior.java.springsecurity.models.Post;
import junior.java.springsecurity.repositories.PostRepository;
import junior.java.springsecurity.repositories.UserRepository;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void createPost(CreatePostDTO createPostDto, String token) {
        var user = userRepository.findById((UUID.fromString(token)))
                .orElseThrow(() -> new RuntimeException("User not found"));

        var post = new Post();
        post.setContent(createPostDto.content());
        post.setUser(user);

        postRepository.save(post);
    }

    public FeedDTO listPosts(int page, int pageSize) {

        var pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp");

        var postsPage = postRepository.findAll(pageRequest)
                .map(post -> new FeedItemDTO(
                        post.getPostId(),
                        post.getContent(),
                        post.getUser().getUsername()));

        return new FeedDTO(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements());
    }

    public void deletePost(Long postId, UUID userId) {
        // 1. Busca o post ou lança uma exceção 404 (Not Found) se não existir
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post não encontrado"));

        // 2. Busca o usuário que está fazendo a requisição
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário inválido"));

        // 3. Verifica se o usuário é o dono do post
        boolean isOwner = post.getUser().getUserId().equals(userId);

        // 4. Verifica se o usuário tem a permissão de ADMIN
        boolean isAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));

        // 5. Se não for o dono NEM admin, lança uma exceção 403 (Forbidden)
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este post");
        }

        // 6. Se a verificação passar, deleta o post
        postRepository.deleteById(postId);
    }
}