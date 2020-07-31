package com.oyekanmiayo.findtreasureapp.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TreasureDetails {
    @JsonProperty("total")
    int total;
    @JsonProperty("found")
    int found;
}
