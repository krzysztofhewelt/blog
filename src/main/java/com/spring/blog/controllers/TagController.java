package com.spring.blog.controllers;

import com.github.slugify.Slugify;
import com.spring.blog.dto.TagDto;
import com.spring.blog.models.Post;
import com.spring.blog.models.Tag;
import com.spring.blog.service.*;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class TagController {

    private final TagService tagService;
    private final PostService postService;
    private final ModelMapper modelMapper;

    @Autowired
    public TagController(TagService tagService, PostService postService)
    {
        this.tagService = tagService;
        this.postService = postService;
        this.modelMapper = new ModelMapper();
    }

    @RequestMapping("/tags")
    public String index(Model model)
    {
        List<Tag> tags = tagService.findAll();
        model.addAttribute("tags", tags);

        return "tags/showAll";
    }

    @RequestMapping("/tags/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createNewCategoryForm(Tag tag)
    {
        return "admin/tags/create";
    }

    @PostMapping("/tags/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String createNewCategoryPost(@Valid @ModelAttribute("tag") TagDto tagDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
            return "admin/tags/create";

        Tag tag = mapDtoToTag(tagDto);
        Slugify slug = Slugify.builder().build();
        tag.setSlug(slug.slugify(tag.getName()));

        Date now = new Date();
        tag.setCreatedAt(now);

        try {
            tagService.saveOrUpdate(tag);
        } catch(Exception e) {
            model.addAttribute("errorExists", "A tag with that name already exists.");
            return "admin/tags/create";
        }

        return "redirect:/tags";
    }

    @RequestMapping("/tags/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editCategory(@PathVariable("id") Long id, Model model)
    {
        Optional<Tag> tag = tagService.findById(id);

        if(tag.isPresent())
        {
            model.addAttribute("tag", tag.get());
            return "admin/tags/edit";
        }
        else
        {
            return "redirect:/";
        }
    }

    @PostMapping("/tags/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateCategory(@PathVariable("id") Long id, @Valid @ModelAttribute("tag") TagDto tagDto, BindingResult result, Model model)
    {
        Optional<Tag> findTag = tagService.findById(id);

        if(result.hasErrors())
        {
            model.addAttribute("tag", tagDto);
            return "admin/tags/edit";
        }

        if(findTag.isPresent())
        {
            Slugify slug = Slugify.builder().build();
            Date now = new Date();
            Tag tag = findTag.get();

            tag.setName(tagDto.getName());
            tag.setSlug(slug.slugify(tagDto.getName()));
            tag.setUpdatedAt(now);

            try {
                tagService.saveOrUpdate(tag);
            } catch(Exception e) {
                model.addAttribute("tag", findTag);
                model.addAttribute("errorExists", "A tag with that name already exists.");
                return "admin/tags/edit";
            }
        }

        return "redirect:/tags";
    }

    @RequestMapping(value= {"/tags/{slug}", "/tags/{slug}/{pageNumber}"})
    public String showTagOfPost(@PathVariable("slug") String slug, Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Post> page = this.postService.findByTags_Slug(slug, pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Post> posts = page.getContent();

        Tag tag = this.tagService.findBySlug(slug);

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("posts", posts);
        model.addAttribute("pageUrl", "/tags/" + slug + "/");
        model.addAttribute("tag", tag);

        return "posts/showPosts";
    }

    @DeleteMapping("/tags/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String delete(@PathVariable("id") Long id) {
        try {
            this.tagService.deleteById(id);
        } catch(Exception e) {
            System.out.println("Cannot delete");
        }

        return "redirect:/tags";
    }

    private TagDto mapTagToDto(Tag tag)
    {
        return modelMapper.map(tag, TagDto.class);
    }

    private Tag mapDtoToTag(TagDto tagDto)
    {
        return modelMapper.map(tagDto, Tag.class);
    }
}
