package com.portfolio.app.service;


import com.portfolio.app.dao.BlogPostRepository;
import com.portfolio.app.dao.ProfileRepository;
import com.portfolio.app.dto.BlogPostDTO;
import com.portfolio.app.dto.BlogPostSummaryDTO;
import com.portfolio.app.entity.BlogPost;
import com.portfolio.app.entity.Profile;
import com.portfolio.app.exception.BusinessRuleException;
import com.portfolio.app.exception.ResourceNotFoundException;
import com.portfolio.app.mapper.BlogPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final ProfileRepository profileRepository;
    private final BlogPostMapper blogPostMapper;

    @Override
    @Transactional
    public BlogPostDTO createBlogPost(BlogPostDTO blogPostDTO) {
        log.info("Creating new blog post: {}", blogPostDTO.getTitle());

        // Validate profile exists
        Profile profile = profileRepository.findById(blogPostDTO.getProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", blogPostDTO.getProfileId()));

        // Check if slug already exists
        if (blogPostRepository.existsBySlug(blogPostDTO.getSlug())) {
            throw new BusinessRuleException("Slug '" + blogPostDTO.getSlug() + "' is already taken");
        }

        // Set publishedAt if publishing
        if (blogPostDTO.getIsPublished() && blogPostDTO.getPublishedAt() == null) {
            blogPostDTO.setPublishedAt(LocalDateTime.now());
        }

        BlogPost blogPost = blogPostMapper.toEntity(blogPostDTO);
        blogPost.setProfile(profile);

        BlogPost savedBlogPost = blogPostRepository.save(blogPost);
        log.info("Blog post created successfully with ID: {}", savedBlogPost.getId());

        return blogPostMapper.toDto(savedBlogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostDTO getBlogPostById(Long id) {
        log.debug("Fetching blog post by ID: {}", id);

        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        return blogPostMapper.toDto(blogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostDTO getBlogPostBySlug(String slug) {
        log.debug("Fetching blog post by slug: {}", slug);

        BlogPost blogPost = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "slug", slug));

        return blogPostMapper.toDto(blogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPostDTO getPublishedBlogPostBySlug(String slug) {
        log.debug("Fetching published blog post by slug: {}", slug);

        BlogPost blogPost = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "slug", slug));

        if (!blogPost.getIsPublished()) {
            throw new ResourceNotFoundException("BlogPost", "slug", slug);
        }

        // Increment view count
        blogPostRepository.incrementViewCount(blogPost.getId());
        blogPost.setViewCount(blogPost.getViewCount() + 1);

        return blogPostMapper.toDto(blogPost);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getAllBlogPosts(Pageable pageable) {
        log.debug("Fetching all blog posts with pagination");

        return blogPostRepository.findAll(pageable)
                .map(blogPostMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getPublishedBlogPosts(Pageable pageable) {
        log.debug("Fetching published blog posts with pagination");

        return blogPostRepository.findByIsPublishedTrue(pageable)
                .map(blogPostMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getBlogPostsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching blog posts for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return blogPostRepository.findByProfileId(profileId, pageable)
                .map(blogPostMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getPublishedBlogPostsByProfile(Long profileId, Pageable pageable) {
        log.debug("Fetching published blog posts for profile ID: {}", profileId);

        if (!profileRepository.existsById(profileId)) {
            throw new ResourceNotFoundException("Profile", "id", profileId);
        }

        return blogPostRepository.findByProfileIdAndIsPublishedTrue(profileId, pageable)
                .map(blogPostMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> getBlogPostsByTag(String tag, Pageable pageable) {
        log.debug("Fetching blog posts with tag: {}", tag);

//        return blogPostRepository.findByTag(tag, pageable)
//                .map(blogPostMapper::toSummaryDto);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BlogPostSummaryDTO> searchBlogPosts(String query, Pageable pageable) {
        log.debug("Searching blog posts with query: {}", query);

        if (query == null || query.trim().isEmpty()) {
            return getPublishedBlogPosts(pageable);
        }

//        return blogPostRepository.search(query.trim(), pageable)
//                .map(blogPostMapper::toSummaryDto);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostSummaryDTO> getRecentBlogPosts(int limit) {
        log.debug("Fetching {} recent blog posts", limit);

        Pageable pageable = Pageable.ofSize(limit);
        return blogPostRepository.findByIsPublishedTrueOrderByPublishedAtDesc(pageable)
                .getContent()
                .stream()
                .map(blogPostMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostSummaryDTO> getPopularBlogPosts(int limit) {
        log.debug("Fetching {} popular blog posts", limit);

        Pageable pageable = Pageable.ofSize(limit);
        return blogPostRepository.findPopularPosts(pageable)
                .getContent()
                .stream()
                .map(blogPostMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BlogPostDTO updateBlogPost(Long id, BlogPostDTO blogPostDTO) {
        log.info("Updating blog post with ID: {}", id);

        BlogPost existingBlogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        // Check if slug is being changed and if it's available
        if (!existingBlogPost.getSlug().equals(blogPostDTO.getSlug())) {
            if (blogPostRepository.existsBySlug(blogPostDTO.getSlug())) {
                throw new BusinessRuleException("Slug '" + blogPostDTO.getSlug() + "' is already taken");
            }
        }

        // If publishing for the first time, set publishedAt
        if (!existingBlogPost.getIsPublished() && blogPostDTO.getIsPublished()
                && blogPostDTO.getPublishedAt() == null) {
            blogPostDTO.setPublishedAt(LocalDateTime.now());
        }

        blogPostMapper.updateEntityFromDto(blogPostDTO, existingBlogPost);

        // Ensure profile is set
        if (blogPostDTO.getProfileId() != null &&
                !existingBlogPost.getProfile().getId().equals(blogPostDTO.getProfileId())) {
            Profile profile = profileRepository.findById(blogPostDTO.getProfileId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profile", "id", blogPostDTO.getProfileId()));
            existingBlogPost.setProfile(profile);
        }

        BlogPost updatedBlogPost = blogPostRepository.save(existingBlogPost);
        log.info("Blog post updated successfully: {}", id);

        return blogPostMapper.toDto(updatedBlogPost);
    }

    @Override
    @Transactional
    public BlogPostDTO publishBlogPost(Long id) {
        log.info("Publishing blog post with ID: {}", id);

        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        if (!blogPost.getIsPublished()) {
            blogPost.setIsPublished(true);
            if (blogPost.getPublishedAt() == null) {
                blogPost.setPublishedAt(LocalDateTime.now());
            }
            blogPost = blogPostRepository.save(blogPost);
            log.info("Blog post published: {}", id);
        }

        return blogPostMapper.toDto(blogPost);
    }

    @Override
    @Transactional
    public BlogPostDTO unpublishBlogPost(Long id) {
        log.info("Unpublishing blog post with ID: {}", id);

        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        if (blogPost.getIsPublished()) {
            blogPost.setIsPublished(false);
            blogPost = blogPostRepository.save(blogPost);
            log.info("Blog post unpublished: {}", id);
        }

        return blogPostMapper.toDto(blogPost);
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        log.debug("Incrementing view count for blog post ID: {}", id);

        if (!blogPostRepository.existsById(id)) {
            throw new ResourceNotFoundException("BlogPost", "id", id);
        }

        blogPostRepository.incrementViewCount(id);
    }

    @Override
    @Transactional
    public void deleteBlogPost(Long id) {
        log.info("Deleting blog post with ID: {}", id);

        if (!blogPostRepository.existsById(id)) {
            throw new ResourceNotFoundException("BlogPost", "id", id);
        }

        blogPostRepository.deleteById(id);
        log.info("Blog post deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(String slug) {
        return blogPostRepository.existsBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSlugAvailable(String slug, Long excludeId) {
        if (slug == null || slug.trim().isEmpty()) {
            return true;
        }

        if (excludeId == null) {
            return !blogPostRepository.existsBySlug(slug);
        }

        return blogPostRepository.findBySlug(slug)
                .map(blogPost -> blogPost.getId().equals(excludeId))
                .orElse(true);
    }
}
