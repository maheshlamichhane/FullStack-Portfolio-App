//package com.portfolio.app.controller;
//
//import com.portfolio.app.service.*;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/dashboard")
//@RequiredArgsConstructor
//@Tag(name = "Dashboard", description = "Dashboard and statistics APIs")
//@Slf4j
//public class DashboardController {
//
//    private final ProfileService profileService;
//    private final BlogService blogService;
//    private final ContactService contactService;
//    private final ProjectService projectService;
//    private final SkillService skillService;
//    private final ExperienceService experienceService;
//
//    @GetMapping("/overview/{profileId}")
//    @Operation(summary = "Get comprehensive dashboard overview")
//    public ResponseEntity<Map<String, Object>> getDashboardOverview(@PathVariable Long profileId) {
//        Map<String, Object> dashboard = new HashMap<>();
//
//        // Profile Stats
//        dashboard.put("profile", profileService.getProfileById(profileId));
//
//        // Blog Stats
//        Map<String, Object> blogStats = new HashMap<>();
//        blogStats.put("totalPosts", blogService.getBlogStats(profileId).getTotalPublished());
//        blogStats.put("recentPosts", blogService.getRecentBlogPosts(5));
//        blogStats.put("popularPosts", blogService.getPopularBlogPosts(5));
//        dashboard.put("blog", blogStats);
//
//        // Contact Stats
//        Map<String, Object> contactStats = new HashMap<>();
//        contactStats.put("unreadMessages", contactService.getUnreadCount(profileId));
//        dashboard.put("contact", contactStats);
//
//        // Project Stats
//        Map<String, Object> projectStats = projectService.getProjectStats(profileId);
//        dashboard.put("projects", projectStats);
//
//        // Skill Stats
//        Map<String, Object> skillStats = skillService.getSkillStats(profileId);
//        dashboard.put("skills", skillStats);
//
//        // Experience Stats
//        Map<String, Object> experienceStats = experienceService.getExperienceStats(profileId);
//        dashboard.put("experience", experienceStats);
//
//        // Summary
//        Map<String, Object> summary = new HashMap<>();
//        summary.put("lastUpdated", LocalDate.now().toString());
//        summary.put("totalItems", calculateTotalItems(dashboard));
//        dashboard.put("summary", summary);
//
//        return ResponseEntity.ok(dashboard);
//    }
//
//    @GetMapping("/metrics/{profileId}")
//    @Operation(summary = "Get key metrics")
//    public ResponseEntity<Map<String, Long>> getKeyMetrics(@PathVariable Long profileId) {
//        Map<String, Long> metrics = new HashMap<>();
//
//        metrics.put("totalBlogPosts", blogService.getBlogStats(profileId).getTotalPublished());
//        metrics.put("totalProjects", projectService.getProjectStats(profileId).getTotalProjects());
//        metrics.put("totalSkills", skillService.getSkillStats(profileId).getTotalSkills());
//        metrics.put("totalExperienceYears", experienceService.calculateTotalExperienceYears(profileId).longValue());
//        metrics.put("unreadMessages", contactService.getUnreadCount(profileId));
//
//        return ResponseEntity.ok(metrics);
//    }
//
//    private Long calculateTotalItems(Map<String, Object> dashboard) {
//        long total = 0;
//
//        Map<String, Object> blog = (Map<String, Object>) dashboard.get("blog");
//        if (blog != null) {
//            total += (Long) blog.get("totalPosts");
//        }
//
//        Map<String, Object> projects = (Map<String, Object>) dashboard.get("projects");
//        if (projects != null) {
//            total += (Long) projects.get("totalProjects");
//        }
//
//        Map<String, Object> skills = (Map<String, Object>) dashboard.get("skills");
//        if (skills != null) {
//            total += (Long) skills.get("totalSkills");
//        }
//
//        return total;
//    }
//}
