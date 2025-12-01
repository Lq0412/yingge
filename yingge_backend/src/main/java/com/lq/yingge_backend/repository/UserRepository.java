package com.lq.yingge_backend.repository;

import com.lq.yingge_backend.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUserAccountAndIsDelete(String userAccount, Integer isDelete);

    Optional<User> findByUserAccountAndIsDelete(String userAccount, Integer isDelete);
}

