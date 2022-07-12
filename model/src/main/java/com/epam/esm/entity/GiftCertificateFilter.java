package com.epam.esm.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public record GiftCertificateFilter(
        List<@NotBlank @Size(max=45) String> tagNames,

        @Size(max = 45)
        String certificateName,

        @Size(max = 45)
        String certificateDescription) {
}


