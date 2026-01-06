package vn.hcmute.trainingpoints.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.hcmute.trainingpoints.entity.user.User;
import vn.hcmute.trainingpoints.service.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin("*")   // cho Android / web gọi thoải mái
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
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }
}
