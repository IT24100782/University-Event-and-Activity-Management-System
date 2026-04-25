package com.unievent.controller;

import com.unievent.service.ClubService;
import com.unievent.service.EventService;
import com.unievent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private ClubService clubService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userService.getAllUsers().size();
        long activeEvents = eventService.getUpcomingEvents().size();
        
        long pendingUsers = userService.getPendingUsers().size();
        long pendingEvents = eventService.getPendingEvents().size();
        long pendingClubs = clubService.getPendingClubs().size();
        
        stats.put("totalUsers", totalUsers);
        stats.put("activeEvents", activeEvents);
        stats.put("pendingApprovals", pendingUsers + pendingEvents + pendingClubs);
        stats.put("systemHealth", "98%"); 
        
        return ResponseEntity.ok(stats);
    }
}
