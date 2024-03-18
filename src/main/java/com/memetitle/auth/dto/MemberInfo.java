package com.memetitle.auth.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.*;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberInfo  {

    @JsonProperty("sub")
    private String snsTokenId;

    @JsonProperty("email")
    private String snsLoginId;

    @JsonProperty("nickname")
    private String nickname;

}