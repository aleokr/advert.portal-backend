package com.app.advert.portal.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
@Getter
public class CompanyDto {

    Long id;

    String name;

    String description;
}
