package com.nscet.cms.core.session;

import com.nscet.cms.db.entity.User;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserSession {

    private User currentUser;
    private String currentAcademicYear;
    private String portalType; // ADMIN, ACCOUNTS, PAYROLL

    public void login(User user) {
        this.currentUser = user;
        this.portalType = determinePortal(user);
        this.currentAcademicYear = "2025-26";
    }

    public void logout() {
        this.currentUser = null;
        this.portalType = null;
        this.currentAcademicYear = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(String roleName) {
        return currentUser != null &&
               currentUser.getRoles().stream()
                   .anyMatch(role -> role.getName().equals(roleName));
    }

    private String determinePortal(User user) {
        if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ADMIN"))) {
            return "ADMIN";
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("ACCOUNTS"))) {
            return "ACCOUNTS";
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().equals("PAYROLL"))) {
            return "PAYROLL";
        }
        return "VIEWER";
    }
}
