package co.devskills.springbootboilerplate.dto.healt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record HealthCheckResponse (
    @JsonProperty("description") String description
) {
}
