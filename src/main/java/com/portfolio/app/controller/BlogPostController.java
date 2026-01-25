package com.portfolio.app.controller;

import com.portfolio.app.dto.BlogPostDTO;
import com.portfolio.app.dto.BlogPostSummaryDTO;
import com.portfolio.app.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
@Tag(name = "Blog", description = "Blog post management APIs")
@Slf4j
public class BlogPostController {

    private final BlogPostService blogPostService;

    @PostMapping
    @Operation(summary = "Create a new blog post")
    public ResponseEntity<BlogPostDTO> createBlogPost(
            @Valid @RequestBody BlogPostDTO blogPostDTO) {
        log.info("POST /api/v1/blog - Creating new blog post");
        BlogPostDTO createdBlogPost = blogPostService.createBlogPost(blogPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlogPost);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing blog post")
    public ResponseEntity<BlogPostDTO> updateBlogPost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPostDTO blogPostDTO) {
        log.info("PUT /api/v1/blog/{} - Updating blog post", id);
        BlogPostDTO updatedBlogPost = blogPostService.updateBlogPost(id, blogPostDTO);
        return ResponseEntity.ok(updatedBlogPost);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get blog post by ID")
    public ResponseEntity<BlogPostDTO> getBlogPostById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean incrementViews) {
        log.debug("GET /api/v1/blog/{} - Fetching blog post", id);
        if (incrementViews) {
            blogPostService.incrementViewCount(id);
        }
        BlogPostDTO blogPost = blogPostService.getBlogPostById(id);
        return ResponseEntity.ok(blogPost);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get blog post by slug")
    public ResponseEntity<BlogPostDTO> getBlogPostBySlug(
            @PathVariable String slug,
            @RequestParam(defaultValue = "true") boolean publishedOnly,
            @RequestParam(defaultValue = "true") boolean incrementViews) {
        log.debug("GET /api/v1/blog/slug/{} - Fetching blog post", slug);

        BlogPostDTO blogPost;
        if (publishedOnly) {
            blogPost = blogPostService.getPublishedBlogPostBySlug(slug);
        } else {
            blogPost = blogPostService.getBlogPostBySlug(slug);
        }

        if (incrementViews && publishedOnly) {
            blogPostService.incrementViewCount(blogPost.getId());
        }

        return ResponseEntity.ok(blogPost);
    }

    @GetMapping
    @Operation(summary = "Get all blog posts with pagination")
    public ResponseEntity<Page<BlogPostSummaryDTO>> getAllBlogPosts(
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            @RequestParam(required = false) Long profileId,
            @RequestParam(required = false) Boolean publishedOnly) {
        log.debug("GET /api/v1/blog - Fetching all blog posts");

        Page<BlogPostSummaryDTO> blogPosts;
        if (profileId != null) {
            if (publishedOnly != null && publishedOnly) {
                blogPosts = blogPostService.getPublishedBlogPostsByProfile(profileId, pageable);
            } else {
                blogPosts = blogPostService.getBlogPostsByProfile(profileId, pageable);
            }
        } else if (publishedOnly != null && publishedOnly) {
            blogPosts = blogPostService.getPublishedBlogPosts(pageable);
        } else {
            blogPosts = blogPostService.getAllBlogPosts(pageable);
        }

        return ResponseEntity.ok(blogPosts);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get blog posts by tag")
    public ResponseEntity<Page<BlogPostSummaryDTO>> getBlogPostsByTag(
            @PathVariable String tag,
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/v1/blog/tag/{} - Fetching blog posts by tag", tag);
        Page<BlogPostSummaryDTO> blogPosts = blogPostService.getBlogPostsByTag(tag, pageable);
        return ResponseEntity.ok(blogPosts);
    }

    @GetMapping("/search")
    @Operation(summary = "Search blog posts")
    public ResponseEntity<Page<BlogPostSummaryDTO>> searchBlogPosts(
            @RequestParam String query,
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.debug("GET /api/v1/blog/search?query={} - Searching blog posts", query);
        Page<BlogPostSummaryDTO> blogPosts = blogPostService.searchBlogPosts(query, pageable);
        return ResponseEntity.ok(blogPosts);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent blog posts")
    public ResponseEntity<List<BlogPostSummaryDTO>> getRecentBlogPosts(
            @RequestParam(defaultValue = "5") int limit) {
        log.debug("GET /api/v1/blog/recent?limit={} - Fetching recent blog posts", limit);
        List<BlogPostSummaryDTO> recentPosts = blogPostService.getRecentBlogPosts(limit);
        return ResponseEntity.ok(recentPosts);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular blog posts")
    public ResponseEntity<List<BlogPostSummaryDTO>> getPopularBlogPosts(
            @RequestParam(defaultValue = "5") int limit) {
        log.debug("GET /api/v1/blog/popular?limit={} - Fetching popular blog posts", limit);
        List<BlogPostSummaryDTO> popularPosts = blogPostService.getPopularBlogPosts(limit);
        return ResponseEntity.ok(popularPosts);
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish a blog post")
    public ResponseEntity<BlogPostDTO> publishBlogPost(@PathVariable Long id) {
        log.info("PATCH /api/v1/blog/{}/publish - Publishing blog post", id);
        BlogPostDTO blogPost = blogPostService.publishBlogPost(id);
        return ResponseEntity.ok(blogPost);
    }

    @PatchMapping("/{id}/unpublish")
    @Operation(summary = "Unpublish a blog post")
    public ResponseEntity<BlogPostDTO> unpublishBlogPost(@PathVariable Long id) {
        log.info("PATCH /api/v1/blog/{}/unpublish - Unpublishing blog post", id);
        BlogPostDTO blogPost = blogPostService.unpublishBlogPost(id);
        return ResponseEntity.ok(blogPost);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a blog post")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
        log.info("DELETE /api/v1/blog/{} - Deleting blog post", id);
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-slug")
    @Operation(summary = "Check if slug is available")
    public ResponseEntity<Map<String, Boolean>> checkSlugAvailability(
            @RequestParam String slug,
            @RequestParam(required = false) Long excludeId) {
        log.debug("GET /api/v1/blog/check-slug?slug={} - Checking slug availability", slug);
        boolean isAvailable = blogPostService.isSlugAvailable(slug, excludeId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", isAvailable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats/{profileId}")
    @Operation(summary = "Get blog statistics for a profile")
    public ResponseEntity<Map<String, Object>> getBlogStats(@PathVariable Long profileId) {
        log.debug("GET /api/v1/blog/stats/{} - Getting blog statistics", profileId);

        Map<String, Object> stats = new HashMap<>();

        // Get total published posts count
        long totalPublished = blogPostService.getPublishedBlogPostsByProfile(
                profileId, Pageable.unpaged()).getTotalElements();

        // Get recent posts
        List<BlogPostSummaryDTO> recentPosts = blogPostService.getRecentBlogPosts(5);

        // Get popular posts
        List<BlogPostSummaryDTO> popularPosts = blogPostService.getPopularBlogPosts(5);

        stats.put("totalPublished", totalPublished);
        stats.put("recentPosts", recentPosts);
        stats.put("popularPosts", popularPosts);

        return ResponseEntity.ok(stats);
    }
}
