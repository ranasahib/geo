package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.User;
import com.rd.domain.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    

}