package com.squadify.app.squad;

import com.squadify.app.model.SquadDto;
import com.squadify.app.playlist.PlaylistResponseMapper;
import com.squadify.app.user.SquadifyUserResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class SquadResponseMapper {

    private final PlaylistResponseMapper playlistResponseMapper;
    private final SquadifyUserResponseMapper squadifyUserResponseMapper;

    public SquadDto map(Squad squad) {
        SquadDto squadDto = new SquadDto();
        squadDto.setSquadId(squad.getSquadId());
        squadDto.setName(squad.getName());
        squadDto.setOwner(squadifyUserResponseMapper.map(squad.getOwner()));
        squadDto.setMembers(squadifyUserResponseMapper.map(squad.getMembers()));
        squadDto.setRequests(squadifyUserResponseMapper.map(squad.getRequests()));
        Optional.ofNullable(squad.getPlaylist()).map(playlistResponseMapper::map).ifPresent(squadDto::setPlaylist);
        return squadDto;
    }

    public List<SquadDto> map(List<Squad> squads) {
        return squads.stream()
                .map(this::map)
                .collect(toList());
    }
}
