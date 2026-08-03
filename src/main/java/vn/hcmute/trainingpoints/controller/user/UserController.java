package vn.hcmute.trainingpoints.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAllUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String q
    ) {
        return userService.searchUsers(role, q);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }
}
