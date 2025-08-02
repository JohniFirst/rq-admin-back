package org.example.rq_admin.repository;

import org.example.rq_admin.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRepository extends JpaRepository<UserInfo, Long> {

    boolean existsByUsername(String username);

    UserInfo findByUsername(String username);

    List<UserInfo> findByAuditStatus(boolean b);
}
