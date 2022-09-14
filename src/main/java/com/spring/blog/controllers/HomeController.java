package com.spring.blog.controllers;

import com.spring.blog.models.Post;
import com.spring.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
public class HomeController {

    private final PostService postService;

    @Autowired
    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @RequestMapping(value = {"/"})
    public String index(Model model)
    {
        List<Post> posts = postService.findTop5ByOrderByCreatedAtDesc();

        model.addAttribute("recentPosts", true);
        model.addAttribute("posts", posts);

        return "index";
    }

    @RequestMapping("/about")
    public String about()
    {
        return "about";
    }

    @RequestMapping("/403")
    public String accessDenied() {
        return "error/403";
    }

    @RequestMapping("/404")
    public String notFound() {
        return "error/404";
    }
}