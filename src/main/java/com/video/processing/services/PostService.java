package com.video.processing.services;

import com.video.processing.entities.Post;
import com.video.processing.exceptions.ResourceNotFoundException;
import com.video.processing.repositories.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public Post createNewPost(Post post){
        return this.postRepository.save(post);
    }

    public Page<Post> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findAll(pageable);
    }

    public Post fetchPost(Long id){
        return postRepository.findById(id)
            .orElseThrow(()-> new ResourceNotFoundException("Resource not found with given id: "+id));
    }
}
