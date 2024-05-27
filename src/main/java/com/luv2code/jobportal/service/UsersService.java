package com.luv2code.jobportal.service;

import com.luv2code.jobportal.entity.JobSeekerProfile;
import com.luv2code.jobportal.entity.RecruiterProfile;
import com.luv2code.jobportal.entity.Users;
import com.luv2code.jobportal.repository.JobSeekerProfileRepository;
import com.luv2code.jobportal.repository.RecruiterProfileRepository;
import com.luv2code.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UsersService(UsersRepository usersRepository,RecruiterProfileRepository recruiterProfileRepository,JobSeekerProfileRepository jobSeekerProfileRepository,PasswordEncoder passwordEncoder){
        this.usersRepository=usersRepository;
        this.recruiterProfileRepository=recruiterProfileRepository;
        this.jobSeekerProfileRepository=jobSeekerProfileRepository;
        this.passwordEncoder=passwordEncoder;
    }

    public Users addNew(Users user){
        user.setActive(true);
        user.setRegistrationDate(new Date(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Users savedUser = usersRepository.save(user);

        int userTypeId = user.getUserTypeId().getUserTypeId();
        if(userTypeId==1){
            recruiterProfileRepository.save(new RecruiterProfile(user));
        }else{
            jobSeekerProfileRepository.save(new JobSeekerProfile(user));
        }
        return savedUser;
    }

    public Object getCurrentUserProfile() {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("could not found user"));
            int userId = users.getUserId();
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile recruiterProfile = recruiterProfileRepository.findById(userId).orElse(new RecruiterProfile());
                return recruiterProfile;
            }else{
                JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                return jobSeekerProfile;
            }
        }
        return null;

    }
}
