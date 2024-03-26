package com.memetitle.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor()
@AllArgsConstructor
public class OidcPublicKeys {

    @JsonProperty("keys")
    private List<OidcPublicKey> keys = new ArrayList<>();
}
