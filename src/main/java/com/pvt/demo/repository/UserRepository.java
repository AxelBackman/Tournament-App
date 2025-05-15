package com.pvt.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pvt.demo.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);}
