package com.unievent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "redirect:/signup.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/student-dashboard.html";
    }

    @GetMapping("/student-dashboard")
    public String studentDashboard() {
        return "forward:/student-dashboard.html";
    }

    @GetMapping("/clubadmin-dashboard")
    public String clubadminDashboard() {
        return "forward:/clubadmin-dashboard.html";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "forward:/feedback.html";
    }

    @GetMapping("/clubs")
    public String clubs() {
        return "forward:/clubs.html";
    }

    @GetMapping("/venues")
    public String venues() {
        return "forward:/venues.html";
    }

    @GetMapping("/venue")
    public String venue() {
        return "forward:/venues.html";
    }
    
    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "forward:/signup.html";
    }

    @GetMapping("/signup")
    public String signup() {
        return "forward:/signup.html";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "forward:/admin-dashboard.html";
    }

    @GetMapping("/admin-users")
    public String adminUsers() {
        return "forward:/admin-users.html";
    }

    @GetMapping("/admin-events")
    public String adminEvents() {
        return "forward:/admin-events.html";
    }

    @GetMapping("/event-details")
    public String eventDetails() {
        return "forward:/event-details.html";
    }

    @GetMapping("/event-detail")
    public String eventDetail() {
        return "forward:/event-detail.html";
    }

    @GetMapping("/admin-feedback")
    public String adminFeedback() {
        return "forward:/admin-feedback.html";
    }

    @GetMapping("/admin-clubs")
    public String adminClubs() {
        return "forward:/admin-clubs.html";
    }

    @GetMapping("/my-club")
    public String myClub() {
        return "forward:/my-club.html";
    }

    @GetMapping("/club-detail")
    public String clubDetail() {
        return "forward:/club-detail.html";
    }

    @GetMapping("/events")
    public String events() {
        return "forward:/events.html";
    }

    @GetMapping("/venue-booking")
    public String venueBooking() {
        return "forward:/venue-booking.html";
    }

    @GetMapping("/create-event")
    public String createEvent() {
        return "forward:/create-event.html";
    }

    @GetMapping("/create-club")
    public String createClub() {
        return "forward:/create-club.html";
    }

    @GetMapping("/profile")
    public String profile() {
        return "forward:/profile.html";
    }
}
