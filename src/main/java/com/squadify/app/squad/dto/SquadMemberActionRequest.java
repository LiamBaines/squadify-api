package com.squadify.app.squad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SquadMemberActionRequest {
    private String username;
    private SquadMemberAction action;
}
