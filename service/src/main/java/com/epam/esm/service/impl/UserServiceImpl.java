package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.exception.ResourceNotFoundException;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityExistsException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public Page<User> findAllUser(Pageable pageable) {
        Page<User> users = userDao.findAll(pageable);

        if(users.isEmpty()) {
            throw new ResourceNotFoundException("There are no users on " + pageable.getPageNumber() + " page");
        }

        return users;
    }

    @Override
    public User createUser(User user) {
        if(userDao.existsByEmail(user.getEmail())) {
            throw new EntityExistsException("The user with email " + user.getEmail() + " has already been registered");
        }

        return userDao.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {

        return userDao.findByEmail(email);
    }
}
