package com.iwor.dto;

import java.util.Map;

public record CompanyReadDto(Integer id,
                             String name,
                             Map<String, String> locales) {
}
