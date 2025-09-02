package junior.java.springsecurity.models;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String title;
    private String content;

    @CreationTimestamp
    private Instant creationTimestamp;

    public Long getPostId() {return postId;}

    public void setPostId(Long postId) {this.postId = postId;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user = user;}

    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getContent() {return content;}

    public void setContent(String content) {this.content = content;}

    public Instant getCreationTimestamp() {return creationTimestamp;}

    public void setCreationTimestamp(Instant creationTimestamp) {this.creationTimestamp = creationTimestamp;}
    
}
