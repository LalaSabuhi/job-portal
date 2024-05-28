package com.luv2code.jobportal.repository;

import com.luv2code.jobportal.entity.RecruiterProfile;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Integer> {

}
