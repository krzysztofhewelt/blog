package com.spring.blog.controllers;

import com.spring.blog.dto.UserDto;
import com.spring.blog.models.User;
import com.spring.blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Controller
public class AuthenticateController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthenticateController(UserService userService) {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        if (principal != null) {
            return "redirect:/";
        } else {
            return "auth/login";
        }
    }

    @PostMapping("/login")
    public String afterLogin(Principal principal)
    {
        if(principal != null) {

            Optional<User> user = this.userService.findByEmail(principal.getName());
            if(user.isPresent())
            {
                Date date = new Date();
                user.get().setLastLoginAt(date);

                this.userService.saveOrUpdate(user.get());
            }
        }
        else
        {
            return "auth/login";
        }

        return "redirect:/";
    }

    @RequestMapping("/register")
    public String showRegisterPage(User user)
    {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerNewUser(HttpServletRequest request, @Valid @ModelAttribute("user") UserDto userDto, BindingResult result, Model model) {

        if(result.hasErrors())
            return "auth/register";

        Optional<User> checkUser = userService.findByUsernameOrEmail(userDto.getUsername(), userDto.getEmail());
        if(checkUser.isPresent()) {
            model.addAttribute("exists", "User with that email or/and username already exists");
            return "auth/register";
        }

        User user = mapDtoToUser(userDto);

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setAccountNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setRegisteredAt(new Date());
        user.setAuthority("ROLE_USER");

        userService.saveOrUpdate(user);

        try {
            request.login(user.getEmail(), userDto.getPassword());
        } catch(Exception e)
        {
            System.out.println("Bad credentials");
        }

        return "redirect:/";
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
