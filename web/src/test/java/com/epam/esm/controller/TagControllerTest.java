package com.epam.esm.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TagControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser
    void testShowAllTags() throws Exception {
        mockMvc.perform(get("/tags"))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..tags.size()").value(6),
                        jsonPath("$._links.self.href").value(is("http://localhost/tags?page=0&size=20")),
                        jsonPath("$..tags[*].name")
                                .value(containsInAnyOrder("food", "stationery", "shoe", "virtual", "paper", "by")));
    }

    @Test
    @WithMockUser
    void testShowAllTagsWithPagination() throws Exception {
        mockMvc.perform(get("/tags?page=2&size=2"))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..tags.size()").value(2),
                        jsonPath("$._links.self.href").value(is("http://localhost/tags?page=2&size=2")),
                        jsonPath("$..tags[*].name").value(containsInAnyOrder("stationery", "virtual")));
    }

    @Test
    @WithAnonymousUser
    void testShowAllTagsByAnonymous() throws Exception {
        mockMvc.perform(get("/tags"))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }

    @Test
    @WithMockUser
    void testShowTag() throws Exception {
        mockMvc.perform(get("/tags/{id}", 3))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..name").value("shoe"),
                        jsonPath("$._links.self.href").value(is("http://localhost/tags/3")));
    }

    @Test
    @WithMockUser
    void testShowNotExistedTag() throws Exception {
        mockMvc.perform(get("/tags/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Tag with id 10 wasn't found")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    @WithAnonymousUser
    void testShowTagByAnonymous() throws Exception {
        mockMvc.perform(get("/tags/{id}", 3))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }

    @Test
    @WithMockUser
    void testShowMostPopularHighestPriceTag() throws Exception {
        mockMvc.perform(get("/tags/highest"))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..tags[*].name")
                                .value(containsInAnyOrder("stationery", "paper", "by")));
    }

    @Test
    @Transactional
    @Rollback
    @WithMockUser(roles = "ADMIN")
    void testAddTag() throws Exception {
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"tag\"}"))
                .andDo(log())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.name").value("tag"),
                        jsonPath("$._links.self.href").value("http://localhost/tags/7"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddTagWithNotValidBodyData() throws Exception {
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"t\"}"))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.name")
                                .value(is("size must be between 2 and 45")),
                        jsonPath("$.errorCode").value(is(40015)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddExistedTag() throws Exception {
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"food\"}"))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("The tag with name food has already been existed in database")),
                        jsonPath("$.errorCode").value(is(40035)));
    }

    @Test
    @WithMockUser
    void testAddTagByUserRole() throws Exception {
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"tag\"}"))
                .andDo(log())
                .andExpectAll(status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message").value(is("Access is denied")),
                        jsonPath("$.errorCode").value(is(40301)));
    }

    @Test
    @WithAnonymousUser
    void testAddTagByAnonymous() throws Exception {
        mockMvc.perform(post("/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"tag\"}"))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testDeleteTag() throws Exception {
        mockMvc.perform(delete("/tags/{id}", 1))
                .andDo(log())
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tags"))
                .andDo(log())
                .andExpect(jsonPath("$..tags.size()").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteNotExistedTag() throws Exception {
        mockMvc.perform(delete("/tags/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("No class com.epam.esm.entity.Tag entity with id 10 exists!")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    @WithMockUser
    void testDeleteTagByUser() throws Exception {
        mockMvc.perform(delete("/tags/{id}", 1))
                .andDo(log())
                .andExpectAll(status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message").value(is("Access is denied")),
                        jsonPath("$.errorCode").value(is(40301)));
    }

    @Test
    @WithAnonymousUser
    void testDeleteTagByAnonymous() throws Exception {
        mockMvc.perform(delete("/tags/{id}", 1))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Full authentication is required to access this resource")),
                        jsonPath("$.errorCode").value(is(40101)));
    }
}