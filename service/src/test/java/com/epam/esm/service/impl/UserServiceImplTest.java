package com.epam.esm.service.impl;

import com.epam.esm.dao.UserDao;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserRole;
import com.epam.esm.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import javax.persistence.EntityExistsException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserDao userDao;

    List<User> userList;
    Page<User> users;

    @BeforeEach
    void init() {
        Pageable pageable = PageRequest.of(1, 10);
        userList = List.of(new User(1, "Margo", "Singer"),
                new User(2, "Andrey", "Spider"));

        users = new PageImpl<>(userList, pageable, 2);
    }

    @Test
    void testFindAllUser() {
        Pageable pageable = PageRequest.of(1, 10);
        when(userDao.findAll(pageable)).thenReturn(users);

        Page<User> expected = new PageImpl<>(userList, PageRequest.of(1, 10), 2);
        Page<User> actual = userService.findAllUser(pageable);

        assertEquals(expected, actual);
    }

    @Test
    void testFindAllUserNotExistedPage() {
        Pageable pageable = PageRequest.of(100, 10);
        when(userDao.findAll(pageable)).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findAllUser(pageable));
    }


    @Test
    void testCreateUser() {
        User user = User.builder()
                .email("Uliana_Baby@tut.by")
                .password("xxcASD123")
                .firstName("Uliana")
                .lastName("Baby")
                .role(UserRole.USER)
                .payments(Collections.emptyList())
                .build();
        when(userDao.existsByEmail(user.getEmail())).thenReturn(false);
        when(userDao.save(user)).thenReturn(user);

        User actual = userService.createUser(user);

        assertEquals(user, actual);
    }

    @Test
    void testExistedCreateUser() {
        User user = User.builder().email("anyemail@email.com").build();
        when(userDao.existsByEmail("anyemail@email.com")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void testFindUserByEmail() {
        String email = "email@mail.com";
        Optional<User> user = Optional.of(new User());
        when(userDao.findByEmail(email)).thenReturn(user);

        Optional<User> actual = userService.findByEmail(email);
        assertEquals(user, actual);
    }

    @Test
    void testFindNotExistedUserByEmail() {
        String email = "email@mail.com";
        Optional<User> user = Optional.empty();
        when(userDao.findByEmail(email)).thenReturn(Optional.empty());

        Optional<User> actual = userService.findByEmail(email);
        assertEquals(user, actual);
    }
}