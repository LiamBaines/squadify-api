package com.squadify.app.user;

import com.squadify.app.model.SquadifyUserAndSquadsDto;
import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SquadifyUserAndSquadsResponseMapper {

    private final SquadResponseMapper squadResponseMapper;
    private final SquadifyUserResponseMapper squadifyUserResponseMapper;

    public SquadifyUserAndSquadsDto map(SquadifyUser squadifyUser, List<Squad> squads) {
        SquadifyUserAndSquadsDto response = new SquadifyUserAndSquadsDto();
        response.setUser(squadifyUserResponseMapper.map(squadifyUser));
        response.setSquads(squadResponseMapper.map(squads));
        return response;
    }

}
