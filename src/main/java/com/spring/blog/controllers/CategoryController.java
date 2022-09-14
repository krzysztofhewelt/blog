package com.spring.blog.controllers;

import com.github.slugify.Slugify;
import com.spring.blog.dto.CategoryDto;
import com.spring.blog.models.Category;
import com.spring.blog.models.Post;
import com.spring.blog.service.CategoryService;
import com.spring.blog.service.PostService;
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
public class CategoryController {
    private final CategoryService categoryService;
    private final PostService postService;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, PostService postService)
    {
        this.categoryService = categoryService;
        this.postService = postService;
        this.modelMapper = new ModelMapper();
    }

    @RequestMapping("/categories")
    public String showAllCategories(Model model)
    {
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "categories/showAll";
    }

    @RequestMapping(value={"/categories/{slug}", "/categories/{slug}/{pageNumber}"})
    public String showPostOfCategory(@PathVariable("slug") String slug, Model model, @PathVariable Optional<Integer> pageNumber)
    {
        if(pageNumber.isEmpty())
            pageNumber = Optional.of(1);

        Page<Post> page = this.postService.findByCategories_Slug(slug, pageNumber.get());
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();
        List<Post> posts = page.getContent();

        Optional<Category> category = this.categoryService.findBySlug(slug);

        model.addAttribute("currentPage", pageNumber.get());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("posts", posts);
        model.addAttribute("pageUrl", "/categories/" + slug + "/");
        model.addAttribute("category", category.get());

        return "posts/showPosts";
    }

    @RequestMapping("/categories/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createNewCategoryForm(Category category)
    {
        return "admin/categories/create";
    }

    @PostMapping("/categories/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String createNewCategoryPost(@Valid @ModelAttribute("category") CategoryDto categoryDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
            return "admin/categories/create";

        Category category = mapDtoToCategory(categoryDto);

        Slugify slug = Slugify.builder().build();
        category.setSlug(slug.slugify(category.getName()));

        Date now = new Date();
        category.setCreatedAt(now);

        try {
            categoryService.saveOrUpdate(category);
        } catch(Exception e) {
            model.addAttribute("errorExists", "Name of category already exists!");
            return "admin/categories/create";
        }

        return "redirect:/categories";
    }

    @RequestMapping("/categories/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editCategoryForm(@PathVariable("id") Long id, Model model)
    {
        Optional<Category> category = categoryService.findById(id);

        if(category.isEmpty())
            return "redirect:/categories";

        model.addAttribute("category", category.get());
        return "admin/categories/edit";
    }

    @PostMapping("/categories/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String editCategoryPost(@PathVariable("id") Long id, @Valid @ModelAttribute("category") CategoryDto categoryDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("category", categoryDto);
            return "admin/categories/edit";
        }

        Optional<Category> findCategory = categoryService.findById(id);

        if(findCategory.isPresent())
        {
            Slugify slug = Slugify.builder().build();
            Date now = new Date();
            Category category = findCategory.get();

            category.setName(categoryDto.getName());
            category.setSlug(slug.slugify(categoryDto.getName()));
            category.setUpdatedAt(now);

            try {
                categoryService.saveOrUpdate(category);
            } catch(Exception e) {
                model.addAttribute("errorExists", "A category of that name already exists.");
                return "admin/categories/edit";
            }
        }

        return "redirect:/categories";
    }

    @DeleteMapping("/categories/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(@PathVariable("id") Long id)
    {
        try {
            this.categoryService.deleteById(id);
        } catch(Exception e)
        {
            System.out.println("Cannot delete");
        }

        return "redirect:/categories";
    }

    private CategoryDto mapCategoryToDto(Category category)
    {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category mapDtoToCategory(CategoryDto categoryDto)
    {
        return modelMapper.map(categoryDto, Category.class);
    }
}
