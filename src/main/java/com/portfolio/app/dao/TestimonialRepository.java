package com.portfolio.app.dao;

import com.portfolio.app.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    List<Testimonial> findByProfileId(Long profileId);

    List<Testimonial> findByIsFeaturedTrue();

    List<Testimonial> findByIsApprovedTrue();

    List<Testimonial> findByIsFeaturedTrueAndIsApprovedTrue();

    @Query("SELECT t FROM Testimonial t WHERE t.profile.id = :profileId AND t.isApproved = true ORDER BY t.givenDate DESC")
    List<Testimonial> findApprovedByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT t FROM Testimonial t WHERE t.profile.id = :profileId AND t.isFeatured = true AND t.isApproved = true")
    List<Testimonial> findFeaturedByProfileId(@Param("profileId") Long profileId);

    List<Testimonial> findByProfileIdAndIsApprovedTrueOrderByGivenDateDesc(Long profileId);

    long countByProfileId(Long profileId);

    long countByProfileIdAndIsApprovedTrue(Long profileId);

    @Query("SELECT AVG(t.rating) FROM Testimonial t WHERE t.profile.id = :profileId AND t.isApproved = true")
    Optional<Double> findAverageRatingByProfileId(@Param("profileId") Long profileId);
}
