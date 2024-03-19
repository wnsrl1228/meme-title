package com.memetitle.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor()
public class OidcPublicKey {

    @JsonProperty("kid")
    private String kid;

    @JsonProperty("kty")
    private String kty;

    @JsonProperty("alg")
    private String alg;

    @JsonProperty("use")
    private String use;

    // 공개키는 n과 e의 쌍으로 구성됨
    @JsonProperty("n")
    private String modulus;

    @JsonProperty("e")
    private String exponent;
}