package com.wiltech.insurly.account.cars;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.wiltech.insurly.libraries.rest.BaseDTO;
import com.wiltech.insurly.libraries.rest.CustomNullSerializer;
import com.wiltech.insurly.quote.PrimaryUse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonRootName("car")
@Value
@Builder
public class UserCarResource extends BaseDTO {

    @JsonProperty("id")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    UUID id;

    @JsonProperty("make")
    @NotEmpty(message = "{userCar.make.blank}")
    String make;

    @JsonProperty("model")
    @NotEmpty(message = "{userCar.model.blank}")
    String model;

    @JsonProperty("year")
    @NotNull
    @Min(1900)
    Integer year;

    @JsonProperty("vin")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String vin;

    @JsonProperty("primaryUse")
    @NotNull
    PrimaryUse primaryUse;

    @JsonProperty("nickname")
    @JsonSerialize(nullsUsing = CustomNullSerializer.class)
    String nickname;
}
