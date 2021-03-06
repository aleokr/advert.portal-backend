package com.app.advert.portal.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

}
