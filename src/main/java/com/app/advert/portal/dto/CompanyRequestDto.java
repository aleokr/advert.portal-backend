package com.app.advert.portal.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {

    Long id;

    @NotEmpty
    String name;

    @NotEmpty
    String description;

}
