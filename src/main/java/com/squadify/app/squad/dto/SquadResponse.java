package com.squadify.app.squad.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

@Builder
@Value
public class SquadResponse implements Serializable {

    String squadKey;
    String name;
    SquadMemberResponse owner;
    List<SquadMemberResponse> members;
    List<SquadMemberResponse> memberRequests;
    String playlistUrl;

}
