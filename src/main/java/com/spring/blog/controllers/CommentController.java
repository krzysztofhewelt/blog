package com.spring.blog.controllers;

import com.spring.blog.models.Comment;
import com.spring.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService)
    {
        this.commentService = commentService;
    }

    @DeleteMapping("/comments/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteComment(@PathVariable("id") Long id)
    {
        this.commentService.deleteById(id);

        return "redirect:/";
    }

    // get user's comments
    @RequestMapping(value = {"/comments/user/{username}", "/comments/user/{username}/{pageNumber}"})
    public String showUserComments(@PathVariable("username") String username, @PathVariable Optional<Integer> pageNumber, Model model)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Comment> page = this.commentService.findByUser_Username(username, pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Comment> comments = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("comments", comments);
        model.addAttribute("username", username);
        model.addAttribute("pageUrl", "/comments/user/" + username + "/");

        return "comments/showCommentsForUser";
    }
}
