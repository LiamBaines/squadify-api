package com.squadify.app.user;

import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.squad.dto.SquadResponse;
import com.squadify.app.squad.dto.SquadResponseMapper;
import com.squadify.app.user.dto.SquadifyUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SquadifyUserService {

    private final SquadDao squadDao;

    public SquadifyUserResponse mapToSquadifyUserResponse(SquadifyUser user) {
        return SquadifyUserResponse.builder()
                .firstName(user.getFirstName())
                .username(user.getUsername())
                .squads(getSimplifiedSquads(user))
                .build();
    }

    public SquadifyUserResponse responseWithDirect(String redirect) {
        return SquadifyUserResponse.builder()
                .redirect(redirect)
                .build();
    }

    private List<SquadResponse> getSimplifiedSquads(SquadifyUser user) {
        return getAllSquads(user).stream()
                .map(SquadResponseMapper::mapToSquadResponse)
                .collect(toList());

    }

    private List<Squad> getAllSquads(SquadifyUser user) {
        List<Squad> squads = new ArrayList<>();
        squads.addAll(squadDao.findByOwner(user));
        squads.addAll(squadDao.findByMembersContains(user));
        squads.addAll(squadDao.findByMemberRequestsContains(user));
        return squads;
    }

}
