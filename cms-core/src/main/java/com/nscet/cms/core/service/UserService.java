package com.nscet.cms.core.service;

import com.nscet.cms.core.exception.DuplicateResourceException;
import com.nscet.cms.core.exception.ResourceNotFoundException;
import com.nscet.cms.core.security.PasswordUtil;
import com.nscet.cms.db.entity.Role;
import com.nscet.cms.db.entity.User;
import com.nscet.cms.db.repository.RoleRepository;
import com.nscet.cms.db.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordUtil passwordUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordUtil = passwordUtil;
    }

    public Page<User> getAll(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.trim().isEmpty()) {
            return userRepository.search(search.trim(), pageable);
        }
        return userRepository.findAllActive(pageable);
    }

    public User getById(Long id) {
        return userRepository.findByIdActive(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public User create(User user, String rawPassword, Set<Role> roles) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("User", "username", user.getUsername());
        }

        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            user.setPasswordHash(passwordUtil.hashPassword(rawPassword));
        }

        if (roles != null && !roles.isEmpty()) {
            user.setRoles(roles);
        }

        user.setIsActive(true);
        user.setIsLocked(false);
        user.setFailedAttempts(0);

        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User updatedUser, String rawPassword, Set<Role> roles) {
        User existing = getById(id);

        if (userRepository.existsByUsernameAndIdNot(updatedUser.getUsername(), id)) {
            throw new DuplicateResourceException("User", "username", updatedUser.getUsername());
        }

        existing.setUsername(updatedUser.getUsername());
        existing.setFullName(updatedUser.getFullName());
        existing.setEmail(updatedUser.getEmail());
        existing.setStaffId(updatedUser.getStaffId());

        if (updatedUser.getIsLocked() != null) {
            existing.setIsLocked(updatedUser.getIsLocked());
        }

        if (rawPassword != null && !rawPassword.trim().isEmpty()) {
            existing.setPasswordHash(passwordUtil.hashPassword(rawPassword));
        }

        if (roles != null) {
            existing.setRoles(roles);
        }

        return userRepository.save(existing);
    }

    @Transactional
    public void softDelete(Long id) {
        User existing = getById(id);
        existing.setIsActive(false);
        userRepository.save(existing);
    }

    @Transactional
    public void toggleLock(Long id) {
        User existing = getById(id);
        existing.setIsLocked(!Boolean.TRUE.equals(existing.getIsLocked()));
        if (!existing.getIsLocked()) {
            existing.setFailedAttempts(0);
        }
        userRepository.save(existing);
    }
}
