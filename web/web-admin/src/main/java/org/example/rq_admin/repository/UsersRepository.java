package org.example.rq_admin.repository;

import org.example.rq_admin.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByUsername(String username);

    Users findByUsername(String username);

    List<Users> findByAuditStatus(boolean b);
}
