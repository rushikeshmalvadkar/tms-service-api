package com.rmalvadkar.tms;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
@RestController
@Slf4j
public class Application {

    private static final Map<String, VisitInfo> IP_ADDRESS_TO_VISIT_INFO_MAP =
            new ConcurrentHashMap<>();

    /*
    private static final Map<String, Map<String, VisitInfo>> IP_ADDRESS_TO_VISIT_INFO_MAP =
            new ConcurrentHashMap<>();
            /test -> 123 -> visit info
    private List<String> rateLimitedPath = List.of("/test);
    */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(HttpServletRequest request) {
        /*
        1 min -> 5
        6 - Retry after 5 min
         */
//        String ipAddress = request.getRemoteAddr();
//        if (IP_ADDRESS_TO_VISIT_INFO_MAP.containsKey(ipAddress)) {
//            VisitInfo visitInfo = IP_ADDRESS_TO_VISIT_INFO_MAP.get(ipAddress);
//            if (requestUnderWindow(visitInfo)) {
//                int updatedVisitedCount = visitInfo.getVisitCount() + 1;
//                visitInfo.setVisitCount(updatedVisitedCount);
//                if (updatedVisitedCount > 5) {
//                    if (visitInfo.getRateLimitWindowExpiryTime() == null) {
//                        LocalDateTime rateLimitWindowExpiryTime = LocalDateTime.now().plusMinutes(1);
//                        visitInfo.setRateLimitWindowExpiryTime(rateLimitWindowExpiryTime);
//                        visitInfo.setRequestWindowExpiryTime(null);
//                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                            .body("Rate Limited : " + rateLimitWindowExpiryTime);
//                    } else{
//                        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                                .body("Rate Limited : " + visitInfo.getRateLimitWindowExpiryTime());
//                    }
//                }
//            } else {
//                if (visitInfo.getRateLimitWindowExpiryTime() != null && visitInfo.getRateLimitWindowExpiryTime()
//                        .isAfter(LocalDateTime.now())) {
//                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
//                            .body("Rate Limited : " + visitInfo.getRateLimitWindowExpiryTime());
//                } else {
//                    visitInfo.setVisitCount(1);
//                    visitInfo.setRequestWindowExpiryTime(LocalDateTime.now().plusMinutes(1));
//                    visitInfo.setRateLimitWindowExpiryTime(null);
//                }
//            }
//        } else {
//            IP_ADDRESS_TO_VISIT_INFO_MAP.put(ipAddress, new VisitInfo(
//                    LocalDateTime.now(),
//                    1,
//                    LocalDateTime.now().plusMinutes(1)
//            ));
//        }
        return ResponseEntity.ok("Hello");
    }

//    private static boolean requestUnderWindow(VisitInfo visitInfo) {
//        return visitInfo.getRequestWindowExpiryTime() != null &&
//                visitInfo.getRequestWindowExpiryTime().isAfter(LocalDateTime.now());
//    }

}

@Data
class VisitInfo {
    private Integer visitCount;
    private LocalDateTime requestWindowExpiryTime;
    private LocalDateTime rateLimitWindowExpiryTime;


    public VisitInfo(Integer visitCount, LocalDateTime requestWindowExpiryTime) {
        this.visitCount = visitCount;
        this.requestWindowExpiryTime = requestWindowExpiryTime;
    }
}
