package junior.java.springsecurity.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import junior.java.springsecurity.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {}

