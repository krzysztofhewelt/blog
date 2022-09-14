package com.spring.blog.controllers;

import com.spring.blog.dto.UserDto;
import com.spring.blog.models.*;
import com.spring.blog.service.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final PostService postService;
    private final UserService userService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(PostService postService, UserService userService, TagService tagService, CategoryService categoryService, CommentService commentService)
    {
        this.postService = postService;
        this.userService = userService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.modelMapper = new ModelMapper();
    }


    @RequestMapping("/admin")
    public String index()
    {
        return "admin/index";
    }

    @RequestMapping(value={"/admin/comments", "/admin/comments/{pageNumber}"})
    public String showComments(Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Comment> page = commentService.findAll(pageNumber.get());

        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Comment> comments = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("comments", comments);

        return "admin/comments/showAll";
    }

    @RequestMapping(value= {"/admin/users", "/admin/users/{pageNumber}"})
    public String showUsers(Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<User> page = userService.findAll(pageNumber.get());

        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<User> users = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("users", users);

        return "admin/users/showAll";
    }

    @RequestMapping(value = {"/admin/posts", "/admin/posts/{pageNumber}"})
    public String showPosts(Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Post> page = postService.findAll(pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Post> posts = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("posts", posts);

        return "admin/posts/showAll";
    }

    @RequestMapping(value={"/admin/tags", "/admin/tags/{pageNumber}"})
    public String showTags(Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Tag> page = tagService.findAll(pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Tag> tags = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("tags", tags);

        return "admin/tags/showAll";
    }

    @RequestMapping(value = {"/admin/categories", "/admin/categories/{pageNumber}"})
    public String showCategories(Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Category> page = categoryService.findAll(pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Category> categories = page.getContent();

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("categories", categories);

        return "admin/categories/showAll";
    }

    @RequestMapping("/admin/users/create")
    public String createUserByAdminForm(User user)
    {
        return "admin/users/create";
    }

    @PostMapping("/admin/users/create")
    public String createUserByAdminPost(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
            return "admin/users/create";

        User user = mapDtoToUser(userDto);

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        user.setRegisteredAt(new Date());
        user.setAccountNonLocked(!userDto.isAccountNonLocked());

        try {
            this.userService.saveOrUpdate(user);
        } catch(Exception e)
        {
            model.addAttribute("exists", "User with that email or username already exists!");
            return "admin/users/create";
        }


        return "redirect:/admin/users";
    }

    private UserDto mapUserToDto(User user)
    {
        return modelMapper.map(user, UserDto.class);
    }

    private User mapDtoToUser(UserDto userDto)
    {
        return modelMapper.map(userDto, User.class);
    }
}
