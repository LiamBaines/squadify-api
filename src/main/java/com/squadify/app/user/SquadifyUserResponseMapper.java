package com.squadify.app.user;

import com.squadify.app.model.SquadifyUserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
public class SquadifyUserResponseMapper {

    public SquadifyUserDto map(SquadifyUser squadifyUser) {
        SquadifyUserDto squadifyUserDto = new SquadifyUserDto();
        squadifyUserDto.setUsername(squadifyUser.getUsername());
        squadifyUserDto.setName(squadifyUser.getName());
        return squadifyUserDto;
    }

    public List<SquadifyUserDto> map(Set<SquadifyUser> squadifyUsers) {
        return squadifyUsers.stream()
                .map(this::map)
                .collect(toList());
    }

}
