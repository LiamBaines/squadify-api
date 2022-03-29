package com.squadify.app.squad.dto;

import lombok.Value;

import java.io.Serializable;

@Value
class SquadMemberResponse implements Serializable {
    String firstName;
    String username;
}
