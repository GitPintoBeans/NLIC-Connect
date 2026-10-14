
package org.nlic.connect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents the relationship between an NLIC Connect
 * user and an assigned authorization role.
 *
 * Maps to the user_roles table in MySQL.
 *
 * @author James Pinto
 */
@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
    private Long userRoleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "assigned_at",
            insertable = false,
            updatable = false)
    private LocalDateTime assignedAt;

    // Required by JPA
    public UserRole() {
    }

    // Getters and Setters

    public Long getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
