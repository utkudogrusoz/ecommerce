package com.utkudogrusoz.ecommerce.Repository.jpa;

import com.utkudogrusoz.ecommerce.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByUsername(String username);

}
