package com.portfolio.app.service;

import com.portfolio.app.dto.BlogPostDTO;
import com.portfolio.app.dto.BlogPostSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BlogPostService {

    // Create
    BlogPostDTO createBlogPost(BlogPostDTO blogPostDTO);

    // Read
    BlogPostDTO getBlogPostById(Long id);
    BlogPostDTO getBlogPostBySlug(String slug);
    BlogPostDTO getPublishedBlogPostBySlug(String slug);
    Page<BlogPostSummaryDTO> getAllBlogPosts(Pageable pageable);
    Page<BlogPostSummaryDTO> getPublishedBlogPosts(Pageable pageable);
    Page<BlogPostSummaryDTO> getBlogPostsByProfile(Long profileId, Pageable pageable);
    Page<BlogPostSummaryDTO> getPublishedBlogPostsByProfile(Long profileId, Pageable pageable);
    Page<BlogPostSummaryDTO> getBlogPostsByTag(String tag, Pageable pageable);
    Page<BlogPostSummaryDTO> searchBlogPosts(String query, Pageable pageable);
    List<BlogPostSummaryDTO> getRecentBlogPosts(int limit);
    List<BlogPostSummaryDTO> getPopularBlogPosts(int limit);

    // Update
    BlogPostDTO updateBlogPost(Long id, BlogPostDTO blogPostDTO);
    BlogPostDTO publishBlogPost(Long id);
    BlogPostDTO unpublishBlogPost(Long id);
    void incrementViewCount(Long id);

    // Delete
    void deleteBlogPost(Long id);

    // Validation
    boolean existsBySlug(String slug);
    boolean isSlugAvailable(String slug, Long excludeId);
}
