package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.core.security.PasswordUtil;
import com.nscet.cms.db.entity.User;
import com.nscet.cms.db.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    public AuthService(UserRepository userRepository, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    @Transactional
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is deactivated");
        }

        if (user.getIsLocked()) {
            throw new IllegalArgumentException("Account is locked. Contact administrator.");
        }

        if (!passwordUtil.verifyPassword(password, user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new IllegalArgumentException("Invalid username or password");
        }

        user.getRoles().forEach(r -> r.getName()); // Force initialization of roles
        resetFailedAttempts(user);
        return user;
    }

    @Transactional
    public void handleFailedLogin(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);
        if (attempts >= 5) {
            user.setIsLocked(true);
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setIsLocked(false);
        userRepository.save(user);
    }

    @Transactional
    public User createUser(String username, String password, String fullName, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("User", "username", username);
        }

        if (!passwordUtil.isPasswordStrong(password)) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordUtil.hashPassword(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setFailedAttempts(0);

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordUtil.verifyPassword(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        if (!passwordUtil.isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters with uppercase, lowercase, number, and special character");
        }

        user.setPasswordHash(passwordUtil.hashPassword(newPassword));
        userRepository.save(user);
    }
}
