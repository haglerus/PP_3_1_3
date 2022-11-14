package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;


@Controller
public class UsersController {

    private final UserService userService;
    private final RoleService roleService;

    public UsersController( @Autowired UserService userService, @Autowired RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin")
    public String showAllUsers(ModelMap model) {
        model.addAttribute("users", userService.listUsers());
        return "admin";
    }

    @GetMapping(value = "/admin/new")
    public String showCreateUser(ModelMap model) {
        User user = new User();
        model.addAttribute("user", user);
        List<Role> roles = roleService.listRoles();
        model.addAttribute("roles", roles);
        return "user";
    }

    @PostMapping(value = "/admin/new")
    public RedirectView createUser(ModelMap model, @ModelAttribute("user") User user,
                                   @RequestParam("roles") Long[] rolesIds) {

        for (Long id : rolesIds) {
            user.addRole(roleService.getRole(id));
        }
        userService.add(user);
        return new RedirectView("/admin");
    }

    @GetMapping(value = "/admin/{id}")
    public String showUser(ModelMap model, @PathVariable("id") Long id) {
        model.addAttribute("user", userService.getUser(id));
        List<Role> roles = roleService.listRoles();
        model.addAttribute("roles", roles);
        return "user";
    }

    @PostMapping(value = "/admin/{id}")
    public String updateUser(ModelMap model,
                             @PathVariable("id") Long id,
                             @RequestParam("email") String email,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam(value = "roles", required = false) Long[] rolesIds) {
        User user = userService.getUser(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.dropRoles();
        userService.addRoles(user, rolesIds);
        userService.update(user, password);
        model.addAttribute("user", user);
        model.addAttribute("roles", roleService.listRoles());
        return "user";
    }

    @GetMapping(value = "/admin/{id}/delete")
    public RedirectView deleteUser(ModelMap model, @PathVariable("id") Long id) {
        userService.delete(id);
        return new RedirectView("/admin");
    }

    @GetMapping(value = "/user")
    public String showMyPage(ModelMap model) {
        model.addAttribute("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<Role> roles = roleService.listRoles();
        model.addAttribute("roles", roles);
        return "user";
    }
}
