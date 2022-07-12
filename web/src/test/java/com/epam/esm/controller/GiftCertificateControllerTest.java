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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class GiftCertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testShowCertificate() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 2))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.name").value("Belvest"),
                        jsonPath("$._links.self.href")
                                .value("http://localhost/certificates/2"));
    }

    @Test
    void testShowNotExistedCertificate() throws Exception {
        mockMvc.perform(get("/certificates/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("There is no certificate with Id 10 in database")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    void testShowCertificateNotValidId() throws Exception {
        mockMvc.perform(get("/certificates/{id}", -10))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("showCertificate.id: must be greater than 0")),
                        jsonPath("$.errorCode").value(is(40020)));
    }

    @Test
    void testShowTagWithCertificateId() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags", 2))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..tags.size()").value(3),
                        jsonPath("$..tags[*].name").value(containsInAnyOrder("shoe", "paper", "by")),
                        jsonPath("$._links.self.href")
                                .value("http://localhost/certificates/2/tags?page=0&size=20")

                );
    }

    @Test
    void testShowTagWithNotExistedCertificateId() throws Exception {
        mockMvc.perform(get("/certificates/{id}/tags", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Certificate with id 10 has no tags on 0 page")),
                        jsonPath("$.errorCode").value(is(40401)));
    }

    @Test
    void testShowCertificateWithFilter() throws Exception {
        mockMvc.perform(post("/certificates?sort=name,asc&sort=createDate,desc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"certificateDescription" : "tw",
                                "certificateName" : "eV",
                                "tagNames" : ["paper", "by"]}"""))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..giftCertificates.size()").value(3),
                        jsonPath("$..giftCertificates[*].name").value(contains("Belvest", "Evroopt", "Evroopt")),
                        jsonPath("$..giftCertificates[2].description")
                                .value("Buy two bananas"),
                        jsonPath("$._links.self.href")
                                .value(is("http://localhost/certificates?page=0&size=20&sort=name,asc&sort=createDate,desc")));
    }

    @Test
    void testShowCertificateWithoutFilter() throws Exception {
        mockMvc.perform(post("/certificates"))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$..giftCertificates.size()").value(4),
                        jsonPath("$..giftCertificates[*].name")
                                .value(containsInAnyOrder("Oz.by", "Belvest", "Evroopt", "Evroopt")),
                        jsonPath("$._links.self.href")
                                .value(is("http://localhost/certificates?page=0&size=20")));
    }

    @Test
    void testShowCertificateWithFilterNotFound() throws Exception {
        mockMvc.perform(post("/certificates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"certificateDescription\" : \"abra-codabra\"}"))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("There is no certificates with such filter " +
                                        "GiftCertificateFilter[tagNames=null, certificateName=null, " +
                                        "certificateDescription=abra-codabra] in database")),
                        jsonPath("$.errorCode").value(40401));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Rollback
    void testAddCertificate() throws Exception {
        mockMvc.perform(post("/certificates/creating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "certificate" : {
                                        "name" : "Epam",
                                        "description" : "Make another Gift Certificate application",
                                        "price" : 20,
                                        "duration" : 10
                                    },
                                    "tags" : [
                                        {"name" : "it"},
                                        {"name" : "by"}
                                    ]
                                }"""))
                .andDo(log())
                .andExpectAll(status().isCreated(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.name").value(is("Epam")),
                        jsonPath("$._links.self.href").value(is("http://localhost/certificates/5")));

        mockMvc.perform(get("/tags"))
                .andDo(log())
                .andExpectAll(
                        jsonPath("$..tags.size()").value(7),
                        jsonPath("$..tags[*].name")
                                .value(containsInAnyOrder("food", "stationery", "shoe", "virtual", "paper", "by", "it")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testAddCertificateWithNotValidBodyData() throws Exception {
        mockMvc.perform(post("/certificates/creating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "certificate" : {
                                        "name" : "     ",
                                        "price" : 0,
                                        "duration" : -10
                                    },
                                    "tags" : [
                                        {"name" : null},
                                        {"name" : "  "}
                                    ]
                                }"""))
                .andDo(log())
                .andExpectAll(status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError['certificate.name']")
                                .value(is("must not be blank")),
                        jsonPath("$.fieldError['certificate.description']")
                                .value(is("must not be blank")),
                        jsonPath("$.fieldError['certificate.price']")
                                .value(is("must be greater than 0")),
                        jsonPath("$.fieldError['certificate.duration']")
                                .value(is("must be greater than 0")),
                        jsonPath("$.fieldError['tags[].name']")
                                .value(is("must not be blank, must not be blank")));
    }

    @Test
    @WithMockUser
    void testAddCertificateByUserRole() throws Exception {
        mockMvc.perform(post("/certificates/creating"))
                .andDo(log())
                .andExpectAll(status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message").value(is("Access is denied")),
                        jsonPath("$.errorCode").value(is(40301)));
    }

    @Test
    @WithAnonymousUser
    void testAddCertificateByAnonymous() throws Exception {
        mockMvc.perform(post("/certificates/creating"))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value("Full authentication is required to access this resource"),
                        jsonPath("$.errorCode").value(40101));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testUpdateCertificate() throws Exception {
        mockMvc.perform(patch("/certificates/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "certificate" : {
                                        "name" : "MaxCompany",
                                        "description" : "Update success",
                                        "price" : 99,
                                        "duration" : 99
                                    },
                                    "tags" : [
                                        {"name" : "new"},
                                        {"name" : "food"}
                                    ]
                                }"""))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.name").value(is("MaxCompany")),
                        jsonPath("$._links.self.href").value(is("http://localhost/certificates/2")));

        mockMvc.perform(get("/certificates/2/tags"))
                .andDo(log())
                .andExpect(jsonPath("$..tags[*].name")
                        .value(containsInAnyOrder("shoe", "food", "new", "paper", "by")));

        mockMvc.perform(get("/tags"))
                .andDo(log())
                .andExpectAll(
                        jsonPath("$..tags.size()").value(7),
                        jsonPath("$..tags[*].name")
                                .value(containsInAnyOrder("food", "stationery", "shoe", "virtual", "paper", "by", "new")));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testUpdateCertificateTags() throws Exception {
        mockMvc.perform(patch("/certificates/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "certificate" : {
                                    },
                                    "tags" : [
                                        {"name" : "new"},
                                        {"name" : "food"}
                                    ]
                                }"""))
                .andDo(log())
                .andExpectAll(status().isOk(),
                        content().contentType(MediaTypes.HAL_JSON),
                        jsonPath("$.name").value(is("Belvest")),
                        jsonPath("$._links.self.href").value(is("http://localhost/certificates/2")));

        mockMvc.perform(get("/certificates/2/tags"))
                .andDo(log())
                .andExpect(jsonPath("$..tags[*].name")
                        .value(containsInAnyOrder("shoe", "food", "new", "paper", "by")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testUpdateNotExistedCertificate() throws Exception {
        mockMvc.perform(patch("/certificates/{id}", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "certificate" : {}
                                }"""))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("Certificate with id 10 can't be updated. It was not found")));
    }

    @Test
    @WithMockUser
    void testUpdateCertificateByUserRole() throws Exception {
        mockMvc.perform(patch("/certificates/{id}", 4))
                .andDo(log())
                .andExpectAll(status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message").value(is("Access is denied")),
                        jsonPath("$.errorCode").value(is(40301)));
    }

    @Test
    @WithAnonymousUser
    void testUpdateCertificateByAnonymousUser() throws Exception {
        mockMvc.perform(patch("/certificates/{id}", 3))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value("Full authentication is required to access this resource"),
                        jsonPath("$.errorCode").value(40101));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    @Rollback
    void testDeleteCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 1))
                .andDo(log())
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/certificates"))
                .andDo(log())
                .andExpectAll(
                        jsonPath("$..giftCertificates.size()").value(3),
                        jsonPath("$..giftCertificates[*].name")
                                .value(containsInAnyOrder("Belvest", "Evroopt", "Evroopt")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteNotExistedCertificate() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value(is("No class com.epam.esm.entity.GiftCertificate entity with id 10 exists!")));
    }

    @Test
    @WithMockUser
    void testDeleteCertificateByUserRole() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message").value(is("Access is denied")),
                        jsonPath("$.errorCode").value(is(40301)));
    }

    @Test
    @WithAnonymousUser
    void testDeleteCertificateByAnonymous() throws Exception {
        mockMvc.perform(delete("/certificates/{id}", 10))
                .andDo(log())
                .andExpectAll(status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.fieldError.exception_message")
                                .value("Full authentication is required to access this resource"),
                        jsonPath("$.errorCode").value(40101));
    }
}