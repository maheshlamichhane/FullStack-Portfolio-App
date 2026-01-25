package com.portfolio.app.aspect;

import com.portfolio.app.service.VisitorAnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class VisitorTrackingAspect {

    private final VisitorAnalyticsService analyticsService;

    @Before("@annotation(com.portfolio.app.annotation.TrackVisit)")
    public void trackVisit() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            String pageVisited = request.getRequestURI();
            String referrer = request.getHeader("Referer");

            // Extract profile ID from request (could be from path, query param, or session)
            Long profileId = extractProfileId(request);

            if (profileId != null) {
                analyticsService.trackVisit(ipAddress, userAgent, pageVisited, profileId);
            }
        } catch (Exception e) {
            log.error("Error tracking visit", e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private Long extractProfileId(HttpServletRequest request) {
        // Implement based on your application logic
        // Could be from path: /api/profiles/{id}/...
        // Or query param: ?profileId=123
        // Or session attribute
        try {
            String path = request.getRequestURI();
            // Example: extract from /api/profiles/123/visitors
            String[] parts = path.split("/");
            for (int i = 0; i < parts.length; i++) {
                if ("profiles".equals(parts[i]) && i + 1 < parts.length) {
                    return Long.parseLong(parts[i + 1]);
                }
            }

            // Try query parameter
            String profileIdParam = request.getParameter("profileId");
            if (profileIdParam != null) {
                return Long.parseLong(profileIdParam);
            }
        } catch (Exception e) {
            log.warn("Could not extract profile ID from request", e);
        }
        return null;
    }
}
