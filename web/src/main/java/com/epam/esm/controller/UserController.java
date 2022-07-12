package com.epam.esm.controller;

import com.epam.esm.assembler.PaymentModelAssembler;
import com.epam.esm.assembler.UserModelAssembler;
import com.epam.esm.entity.AuthenticationRequestBody;
import com.epam.esm.dto.PaymentDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.entity.User;
import com.epam.esm.entity.UserRole;
import com.epam.esm.security.JwtTokenProvider;
import com.epam.esm.service.PaymentService;
import com.epam.esm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collections;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public static final String USERS = "users";
    public static final String CREATE = "create";

    private final UserService userService;
    private final PaymentService paymentService;
    private final UserModelAssembler userAssembler;
    private final PaymentModelAssembler paymentAssembler;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final PagedResourcesAssembler<User> pagedResourcesUserAssembler;
    private final PagedResourcesAssembler<PaymentDto> pagedResourcesPaymentDtoAssembler;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticate(@Valid @RequestBody AuthenticationRequestBody requestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password()));
        UserDetails principal = (UserDetails) authenticate.getPrincipal();

        return principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(role -> jwtTokenProvider.createToken(principal.getUsername(), role))
                .map(token -> Map.of(
                        "email", principal.getUsername(),
                        "token", token))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Registered user " + requestDto.email() + " has no role"));
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<User> createUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.createUser(createUserWithUserDto(userDto));
        return userAssembler.toModel(user)
                .add(linkTo(UserController.class).withRel(USERS));
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> showAllUsers(Pageable pageable) {
        Page<User> users = userService.findAllUser(pageable);

        return pagedResourcesUserAssembler.toModel(users, userAssembler);
    }

    @GetMapping("/{userId}/payments")
    public CollectionModel<EntityModel<PaymentDto>> showUserPayments(@PathVariable @Positive Integer userId, Pageable pageable) {

        Page<PaymentDto> paymentsByUserId = paymentService.findPaymentsByUserId(userId, pageable);

        return pagedResourcesPaymentDtoAssembler.toModel(paymentsByUserId, paymentAssembler)
                .add(linkTo(methodOn(UserController.class).showAllUsers(pageable)).withRel(USERS))
                .add(linkTo(methodOn(PaymentController.class).createPayment(null)).withRel(CREATE));
    }

    private User createUserWithUserDto(UserDto userDto) {
        return User.builder()
                .email(userDto.email())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .password(encoder.encode(userDto.password()))
                .role(UserRole.USER)
                .payments(Collections.emptyList())
                .build();
    }
}
