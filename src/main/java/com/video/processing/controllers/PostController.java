package com.video.processing.controllers;

import com.video.processing.entities.Post;
import com.video.processing.services.PostService;
import com.video.processing.services.UploadThumbnailService;
import com.video.processing.utilities.ResponseFromApi;
import org.springframework.data.domain.Page;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService pService;
    private final UploadThumbnailService uploadThumbnailService;

    public PostController(
            PostService postService,
            UploadThumbnailService uploadThumbnailService) {
        this.pService = postService;
        this.uploadThumbnailService = uploadThumbnailService;
    }

    @GetMapping
    public ResponseEntity<ResponseFromApi<Page<Post>>> fetchAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> pagePosts = pService.getPosts(page, size);
        ResponseFromApi<Page<Post>> response = ResponseFromApi
                .success(pagePosts, "Posts are fetched successfully.");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ResponseFromApi<Post>> savePost(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail) throws IOException {

        String uploadedFilePath = this.uploadThumbnailService.saveFile(thumbnail);
        logger.info("file path for uploaded file: {}", uploadedFilePath);
        Post post = Post.builder()
                .title(title)
                .description(description)
                .thumbnail(uploadedFilePath)
                .build();

        Post postCreated = this.pService.createNewPost(post);
        ResponseFromApi<Post> responseFromApi = ResponseFromApi.success(postCreated, "Post created successful.");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseFromApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseFromApi<Post>> getPost(@PathVariable(name = "id") Long id) {
        Post post = this.pService.fetchPost(id);
        ResponseFromApi<Post> responseFromApi = ResponseFromApi.success(post, "Post fetched successful.");
        return ResponseEntity.status(HttpStatus.OK).body(responseFromApi);
    }
}
