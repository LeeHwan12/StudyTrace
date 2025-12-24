package com.example.StudyTrace.service;

import com.example.StudyTrace.common.exception.*;
import com.example.StudyTrace.domain.users.RegisterUserDTO;
import com.example.StudyTrace.entity.Users;
import com.example.StudyTrace.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String registerUser(RegisterUserDTO dto){

        if(usersRepository.existsByUsername(dto.getUsername())){
            throw new ConflictException("Username is already in use");
        }

        if(dto.getEmail() != null && !dto.getEmail().isBlank()
                && usersRepository.existsByEmail(dto.getEmail())){
            throw new ConflictException("Email is already in use");
        }

        if(usersRepository.existsByNickname(dto.getNickname())){
            throw new ConflictException("Nickname is already in use");
        }

        if(!dto.getPassword().equals(dto.getPasswordConfirm())){
            throw new BadRequestException("Password confirmation not matched");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Users user = dto.toEntity(encodedPassword);
        usersRepository.save(user);

        return user.getUsername(); // ✅ 자동로그인용
    }
}

