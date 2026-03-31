/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Group1.ShoesOnlineShop.controller;

import Group1.ShoesOnlineShop.dto.ActivityDTO;
import Group1.ShoesOnlineShop.entity.Content;
import Group1.ShoesOnlineShop.entity.Coupon;
import Group1.ShoesOnlineShop.entity.Slider;
import Group1.ShoesOnlineShop.repository.ContentRepository;
import Group1.ShoesOnlineShop.repository.CouponRepository;
import Group1.ShoesOnlineShop.repository.SliderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/internal/MarketingHome")
public class MarketingController {

    @Autowired
    private SliderRepository sliderRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ContentRepository contentRepository;

    @GetMapping
    public String showMarketingHome(Model model) {
        long activeSliders = sliderRepository.countByIsActive(true);
        long runningCoupons = couponRepository.countByIsActive(true);
        long totalContents = contentRepository.count();

        model.addAttribute("activeSliders", activeSliders);
        model.addAttribute("runningCoupons", runningCoupons);
        model.addAttribute("totalContents", totalContents);

        List<ActivityDTO> activities = new ArrayList<>();

        List<Slider> recentSliders = sliderRepository.findTop5ByOrderByCreatedAtDesc();
        for (Slider s : recentSliders) {
            String timeAgo = calculateTimeAgo(s.getCreatedAt());
            activities.add(new ActivityDTO("New slider \"" + s.getSliderTitle() + "\" was created", "Slider Management", timeAgo, s.getCreatedAt(), "var(--accent)"));
        }

        List<Coupon> recentCoupons = couponRepository.findTop5ByOrderByCreatedAtDesc();
        for (Coupon c : recentCoupons) {
            String timeAgo = calculateTimeAgo(c.getCreatedAt());
            activities.add(new ActivityDTO("New coupon \"" + c.getCouponCode() + "\" was created", "Coupon Management", timeAgo, c.getCreatedAt(), "var(--warning)"));
        }

        List<Content> recentContents = contentRepository.findTop5ByOrderByCreatedAtDesc();
        for (Content c : recentContents) {
            String timeAgo = calculateTimeAgo(c.getCreatedAt());
            activities.add(new ActivityDTO("Content \"" + c.getContentTitle() + "\" was created", "Content Management", timeAgo, c.getCreatedAt(), "var(--success)"));
        }

        List<ActivityDTO> recentActivities = activities.stream()
                .sorted(Comparator.comparing(ActivityDTO::getCreatedAt).reversed())
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("recentActivities", recentActivities);

        return "marketing/marketing-home";
    }

    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "Unknown";
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = duration.getSeconds();
        if (seconds < 60) return seconds + "s ago";
        long minutes = seconds / 60;
        if (minutes < 60) return minutes + "m ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        long days = hours / 24;
        return days + "d ago";
    }

    @GetMapping("/activities")
    public String showAllActivities(Model model) {
        List<ActivityDTO> allActivities = new ArrayList<>();

        List<Slider> recentSliders = sliderRepository.findTop50ByOrderByCreatedAtDesc();
        for (Slider s : recentSliders) {
            allActivities.add(new ActivityDTO("New slider \"" + s.getSliderTitle() + "\" was created", "Slider Management", calculateTimeAgo(s.getCreatedAt()), s.getCreatedAt(), "var(--accent)"));
        }

        List<Coupon> recentCoupons = couponRepository.findTop50ByOrderByCreatedAtDesc();
        for (Coupon c : recentCoupons) {
            allActivities.add(new ActivityDTO("New coupon \"" + c.getCouponCode() + "\" was created", "Coupon Management", calculateTimeAgo(c.getCreatedAt()), c.getCreatedAt(), "var(--warning)"));
        }

        List<Content> recentContents = contentRepository.findTop50ByOrderByCreatedAtDesc();
        for (Content c : recentContents) {
            allActivities.add(new ActivityDTO("Content \"" + c.getContentTitle() + "\" was created", "Content Management", calculateTimeAgo(c.getCreatedAt()), c.getCreatedAt(), "var(--success)"));
        }

        List<ActivityDTO> sortedActivities = allActivities.stream()
                .sorted(Comparator.comparing(ActivityDTO::getCreatedAt).reversed())
                .limit(100)
                .collect(Collectors.toList());

        model.addAttribute("activities", sortedActivities);
        return "marketing/marketing-activities";
    }
}

