package com.rmalvadkar.tms;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Map<String, VisitInfo> IP_ADDRESS_TO_VISIT_INFO_MAP =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("inside rate limit filter");
        log.info("path : {}", request.getServletPath());
        String ipAddress = request.getRemoteAddr();
        if (IP_ADDRESS_TO_VISIT_INFO_MAP.containsKey(ipAddress)) {
            VisitInfo visitInfo = IP_ADDRESS_TO_VISIT_INFO_MAP.get(ipAddress);
            if (requestUnderWindow(visitInfo)) {
                int updatedVisitedCount = visitInfo.getVisitCount() + 1;
                visitInfo.setVisitCount(updatedVisitedCount);
                if (updatedVisitedCount > 5) {
                    if (visitInfo.getRateLimitWindowExpiryTime() == null) {
                        LocalDateTime rateLimitWindowExpiryTime = LocalDateTime.now().plusMinutes(1);
                        visitInfo.setRateLimitWindowExpiryTime(rateLimitWindowExpiryTime);
                        visitInfo.setRequestWindowExpiryTime(null);
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("Rate Limited : " + rateLimitWindowExpiryTime);
                        return;
                    } else{
                        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                        response.getWriter().write("Rate Limited : " + visitInfo.getRateLimitWindowExpiryTime());
                        return;
                    }
                }
            } else {
                if (visitInfo.getRateLimitWindowExpiryTime() != null && visitInfo.getRateLimitWindowExpiryTime()
                        .isAfter(LocalDateTime.now())) {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.getWriter().write("Rate Limited : " + visitInfo.getRateLimitWindowExpiryTime());
                    return;
                } else {
                    visitInfo.setVisitCount(1);
                    visitInfo.setRequestWindowExpiryTime(LocalDateTime.now().plusMinutes(1));
                    visitInfo.setRateLimitWindowExpiryTime(null);
                }
            }
        } else {
            IP_ADDRESS_TO_VISIT_INFO_MAP.put(ipAddress, new VisitInfo(
                    1,
                    LocalDateTime.now().plusMinutes(1)
            ));
        }
        filterChain.doFilter(request, response);
    }

    private static boolean requestUnderWindow(VisitInfo visitInfo) {
        return visitInfo.getRequestWindowExpiryTime() != null &&
               visitInfo.getRequestWindowExpiryTime().isAfter(LocalDateTime.now());
    }

}
