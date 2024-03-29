package com.example.postapi.service;

import com.example.postapi.exceptionhandling.BlankPostException;
import com.example.postapi.model.Post;
import com.example.postapi.model.PostComment;
import com.example.postapi.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    RestTemplate restTemplate = new RestTemplate();

    private static Logger logger = LoggerFactory.getLogger(PostServiceImpl.class.getName());

    @Autowired
    PostRepository postRepository;


    @Override
    public Post createPost(Post post, String username) throws BlankPostException {
        if(post.getTitle().isEmpty() || post.getDescription().isEmpty()) {
            logger.error(username + " user tried to create a blank post");
            throw new BlankPostException("Please enter both a title and description");
        } else {
            post.setUsername(username);
        return postRepository.save(post);
        }
    }

    @Override
    public Iterable<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Iterable<Post> getAllPostsByUsername(String username) {

       return postRepository.findAllByUsername(username);

    }

    @Override
    public Long deletePostbyId(Long postId) {
        restTemplate.delete("http://localhost:8083/post/" + postId);
        postRepository.deleteById(postId);
        logger.info("Post id: " + postId + "this post has been deleted");
        return postId;

    }

    @Override
    public String deletePostByUsername(String username) {
        postRepository.deleteByUsername(username);
        return username;

    }


    @Override
    public PostComment getAllCommentsByPostId(Long postId) {
        return restTemplate.getForObject("http://localhost:8083/list/" + postId, PostComment.class);
    }

    @Override
    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Post updatePost(Post post, Long postId) {
        Post savedPost = postRepository.findByPostId(postId);

        if(post.getTitle() != null) savedPost.setTitle(post.getTitle());
        if(post.getDescription() != null) savedPost.setDescription(post.getDescription());

        logger.info("Post id " + postId + "has been updated");
        return postRepository.save(savedPost);
    }

    @Override
    public String sendPostIdRestTemplate(Long postId) {
        Post savedPost = postRepository.findByPostId(postId);
        String username = savedPost.getUsername();
        return restTemplate.getForObject("http://localhost:8081/post/" + username, String.class);
    }

}
