package com.squadify.app.squad.dto;

import com.squadify.app.squad.Squad;
import com.squadify.app.squad.dto.SquadMemberResponse;
import com.squadify.app.squad.dto.SquadResponse;
import com.squadify.app.user.SquadifyUser;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class SquadResponseMapper {

    public static SquadResponse mapToSquadResponse(Squad squad) {
        return SquadResponse.builder()
                .name(squad.getName())
                .squadKey(squad.getSquadKey())
                .owner(new SquadMemberResponse(squad.getOwner().getDisplayName(), squad.getOwner().getUsername()))
                .members(mapToSquadMemberResponse(squad.getMembers()))
                .memberRequests(mapToSquadMemberResponse(squad.getMemberRequests()))
                .playlistUrl(squad.getPlaylistUrl())
                .build();
    }

    private static List<SquadMemberResponse> mapToSquadMemberResponse(Set<SquadifyUser> users) {
        return users.stream()
                .map(user -> new SquadMemberResponse(user.getDisplayName(), user.getUsername()))
                .collect(toList());
    }

}
