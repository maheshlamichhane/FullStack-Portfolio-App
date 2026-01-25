package com.portfolio.app.dao;

import com.portfolio.app.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    // Find by slug
    Optional<BlogPost> findBySlug(String slug);

    // Check if slug exists (for validation)
    boolean existsBySlug(String slug);

    // Find all published posts
    Page<BlogPost> findByIsPublishedTrue(Pageable pageable);

    // Find published posts by profile
    Page<BlogPost> findByProfileIdAndIsPublishedTrue(Long profileId, Pageable pageable);

    // Find all posts by profile (including drafts)
    Page<BlogPost> findByProfileId(Long profileId, Pageable pageable);

    // Find posts by tag
    @Query("SELECT bp FROM BlogPost bp WHERE :tag MEMBER OF bp.tags AND bp.isPublished = true")
    Page<BlogPost> findByTag(@Param("tag") String tag, Pageable pageable);

    // Find posts by multiple tags
    @Query("SELECT bp FROM BlogPost bp WHERE bp.tags IN :tags AND bp.isPublished = true")
    Page<BlogPost> findByTagsIn(@Param("tags") List<String> tags, Pageable pageable);

    // Find featured posts (you can add a featured field to entity if needed)
    @Query("SELECT bp FROM BlogPost bp WHERE bp.isPublished = true ORDER BY bp.viewCount DESC")
    Page<BlogPost> findPopularPosts(Pageable pageable);

    // Search posts by title or content
    @Query("SELECT bp FROM BlogPost bp WHERE " +
            "(LOWER(bp.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(bp.content) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
            "bp.isPublished = true")
    Page<BlogPost> search(@Param("query") String query, Pageable pageable);

    // Find recent posts
    Page<BlogPost> findByIsPublishedTrueOrderByPublishedAtDesc(Pageable pageable);

    // Increment view count
    @Query("UPDATE BlogPost bp SET bp.viewCount = bp.viewCount + 1 WHERE bp.id = :id")
    void incrementViewCount(@Param("id") Long id);

    // Count published posts by profile
    long countByProfileIdAndIsPublishedTrue(Long profileId);
}
