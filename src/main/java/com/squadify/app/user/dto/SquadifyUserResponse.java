package com.squadify.app.user.dto;

import com.squadify.app.squad.dto.SquadResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Builder
@Value
public class SquadifyUserResponse implements Serializable {

    String firstName;
    String username;
    List<SquadResponse> squads;
    String redirect;

}
