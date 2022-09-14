package com.spring.blog.controllers;

import com.github.slugify.Slugify;
import com.spring.blog.dto.CommentDto;
import com.spring.blog.dto.PostDto;
import com.spring.blog.models.*;
import com.spring.blog.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class PostController {

    private final PostService postService;
    private final TagService tagService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final UserService userService;

    @Autowired
    public PostController(PostService postService, TagService tagService, CategoryService categoryService, CommentService commentService, UserService userService)
    {
        this.postService = postService;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @RequestMapping(value = {"/posts", "/posts/{pageNumber}"})
    public String index(Model model, @PathVariable Optional<Integer> pageNumber)
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
        model.addAttribute("pageUrl", "/posts/");
        model.addAttribute("posts", posts);

        return "posts/showPosts";
    }

    @RequestMapping("/posts/show/{slug}")
    public String view(@PathVariable("slug") String slug, Model model)
    {
        Optional<Post> post = this.postService.findBySlug(slug);
        if(post.isEmpty())
            return "redirect:/";

        model.addAttribute("post", post.get());
        return "posts/view";
    }

    @PostMapping("/posts/show/{slug}")
    public String postComment(@PathVariable("slug") String slug, @Valid @ModelAttribute CommentDto commentDto, BindingResult result, Model model, Principal principal)
    {
        Optional<Post> post = this.postService.findBySlug(slug);

        if(post.isEmpty())
            return "redirect:/";

        if(result.hasErrors())
        {
            model.addAttribute(post.get());
            return "posts/view";
        }


        if(Objects.equals(commentDto.getContent(), ""))
        {
            model.addAttribute("error", "Treść komentarza nie może być pusta!");
            model.addAttribute("post", post);
            return "posts/view";
        }

        Comment comment = new Comment();

        comment.setPost(post.get());
        Optional<User> user = this.userService.findByEmail(principal.getName());
        user.ifPresent(comment::setUser);

        comment.setContent(commentDto.getContent());
        comment.setCreatedAt(new Date());
        commentService.save(comment);

        model.addAttribute("post", post.get());

        return "posts/view";
    }

    @RequestMapping("/posts/create")
    @PreAuthorize("isAuthenticated()")
    public String createNewPostForm(Post post, Model model)
    {
        List<Tag> tags = this.tagService.findAll();
        List<Category> categories = this.categoryService.findAll();

        model.addAttribute("tags", tags);
        model.addAttribute("categories", categories);

        return "posts/create";

    }

    @PostMapping("/posts/create")
    @PreAuthorize("isAuthenticated()")
    public String createNewPostPost(@Valid @ModelAttribute PostDto postDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            List<Tag> tags = this.tagService.findAll();
            List<Category> categories = this.categoryService.findAll();

            model.addAttribute("post", postDto);
            model.addAttribute("tags", tags);
            model.addAttribute("categories", categories);
            return "posts/create";
        }

        Post post = new Post();
        String authUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(authUsername);
        post.setAuthorId(user.get());

        post.setTags(postDto.getTags());
        post.setCategories(postDto.getCategories());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());


        Date now = new Date();
        post.setCreatedAt(now);

        // to make slug always unique, add timestamp to the end of the title
        Slugify slugify = Slugify.builder().build();
        String postSlug = post.getTitle() + " " + (new Timestamp(now.getTime()).getTime());
        post.setSlug(slugify.slugify(postSlug));

        postService.saveOrUpdate(post);

        return "redirect:/";
    }

    @RequestMapping("/posts/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model, Authentication authentication) {

        Optional<Post> post = this.postService.findById(id);

        if(post.isEmpty())
            return "redirect:/posts/";

        Post findPost = post.get();

        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
           &&
           !Objects.equals(findPost.getAuthorId().getEmail(), authentication.getName()))
            return "redirect:/posts/";

        model.addAttribute("post", findPost);

        List<Tag> tags = this.tagService.findAll();

        List<Category> categories = this.categoryService.findAll();

        // usuwamy duplikaty tagów
        for(int i = 0; i < tags.size(); i++)
        {
            Long index = tags.get(i).getId();
            if(findPost.getTags().stream().filter(tag -> index.equals(tag.getId())).findFirst().orElse(null) != null)
            {
                tags.remove(i);
            }
        }

        // usuwamy duplikaty kategorii
        for(int i = 0; i < categories.size(); i++)
        {
            Long index = categories.get(i).getId();
            if(findPost.getCategories().stream().filter(category -> index.equals(category.getId())).findFirst().orElse(null) != null)
            {
                categories.remove(i);
            }
        }

        model.addAttribute("tags", tags);
        model.addAttribute("categories", categories);

        return "posts/edit";
    }

    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable("id") Long id, @Valid @ModelAttribute("post") PostDto postDto, BindingResult result, Authentication authentication, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("post", postDto);
            return "posts/edit";
        }

        Optional<Post> postOriginal = postService.findById(id);

        if(postOriginal.isEmpty())
            return "redirect:/";

        Post findPost = postOriginal.get();

        if(!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                &&
                !Objects.equals(findPost.getAuthorId().getEmail(), authentication.getName()))
            return "redirect:/posts/";

        findPost.setCategories(postDto.getCategories());
        findPost.setTags(postDto.getTags());
        findPost.setContent(postDto.getContent());
        findPost.setTitle(postDto.getTitle());

        Date date = new Date();
        findPost.setUpdatedAt(date);

        Slugify slugify = Slugify.builder().build();
        findPost.setSlug(slugify.slugify(postDto.getTitle()));

        postService.saveOrUpdate(findPost);

        return "redirect:/";
    }

    @RequestMapping("/users/{username}/posts")
    public String showUserPosts(@PathVariable("username") String username, Model model)
    {
        List<Post> posts = this.postService.findByAuthorIdUsername(username);

        model.addAttribute("user", username);
        model.addAttribute("posts", posts);

        return "posts/showPosts";
    }

    @DeleteMapping("/posts/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String delete(@PathVariable("id") Long id) {
        this.postService.deleteById(id);

        return "redirect:/posts";
    }
}