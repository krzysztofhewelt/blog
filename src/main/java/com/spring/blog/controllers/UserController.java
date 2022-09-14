package com.spring.blog.controllers;

import com.spring.blog.dto.UserDto;
import com.spring.blog.models.User;
import com.spring.blog.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class UserController {
    private final UserService userService;
    public final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService)
    {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    @RequestMapping("/users/{username}")
    public String view(@PathVariable("username") String username, Model model)
    {
        Optional<User> user = userService.findByUsername(username);

        if(user.isPresent())
            model.addAttribute("user", user.get());
        else
            model.addAttribute("user", null);

        return "users/view";
    }

    @DeleteMapping("/users/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteUser(@PathVariable("id") Long id)
    {
        this.userService.deleteById(id);
        return "redirect:/admin/users";
    }

    @RequestMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String editUserAdminForm(@PathVariable("id") Long id, Model model)
    {
        Optional<User> user = this.userService.findById(id);

        if(user.isPresent())
        {
            model.addAttribute("user", user.get());
            return "admin/users/edit";
        }

        model.addAttribute("notExists", "This user does not exists");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateUserByAdmin(@PathVariable("id") Long id, @Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model)
    {
        if(result.hasErrors())
        {
            model.addAttribute("user", userDto);
            return "admin/users/edit";
        }

        Optional<User> user = this.userService.findById(id);
        if(user.isPresent())
        {
            User getUser = user.get();

            getUser.setPassword(userDto.getPassword());
            getUser.setUsername(userDto.getUsername());
            getUser.setEmail(userDto.getEmail());
            getUser.setAccountNonLocked(userDto.isAccountNonLocked());
            getUser.setAuthority(userDto.getAuthority());

            this.userService.saveOrUpdate(getUser);
        }

        return "redirect:/admin/users";
    }

    @RequestMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public String showDashboard(Principal principal, Model model)
    {
        if(principal != null)
        {
            Optional<User> user = this.userService.findByEmail(principal.getName());

            if(user.isEmpty())
                return "redirect:/";

            model.addAttribute("user", user.get());

            return "users/view";
        }

        return "redirect:/";
    }

    @RequestMapping("/user/change-password")
    @PreAuthorize("isAuthenticated()")
    public String showChangePasswordForm(Principal principal)
    {
        if(principal != null)
        {
            return "users/change-password";
        }

        return "redirect:/";
    }

    @PostMapping("/user/change-password")
    @PreAuthorize("isAuthenticated()")
    public String changePassword(@RequestParam("currentPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 Model model)
    {
        if(principal != null)
        {
            Optional<User> user = this.userService.findByEmail(principal.getName());

            if(user.isEmpty())
                return "redirect:/";

            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(newPassword);
            if(!passwordEncoder.matches(oldPassword, user.get().getPassword()))
            {
                model.addAttribute("errorCurrPass", "Password is invalid");
                return "users/change-password";
            }

            if(!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,255}$", newPassword))
            {
                model.addAttribute("errorNewPass", "Password must contain at least one big letter, one special character, and must be at least 8 chars long.");
                return "users/change-password";
            }

            user.get().setPassword(encodedPassword);
            userService.saveOrUpdate(user.get());
            model.addAttribute("success", true);

            return "users/change-password";
        }

        return "redirect:/";
    }
}
