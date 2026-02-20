package co.devskills.springbootboilerplate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DescriptionResponse (
    @JsonProperty("description") String description
) {
}
