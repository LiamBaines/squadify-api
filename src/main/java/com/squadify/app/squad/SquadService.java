package com.squadify.app.squad;

import com.squadify.app.squad.dto.SquadMemberAction;
import com.squadify.app.user.SquadifyUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final SquadDao squadDao;

    public void addPlaylistUrlToSquad(Squad squad, String playlistUrl) {
        squad.setPlaylistUrl(playlistUrl);
        squadDao.save(squad);
    }

    Squad createSquad(SquadifyUser owner) {
        Squad squad = new Squad();
        squad.setOwner(owner);
        squad.setSquadKey(RandomStringUtils.randomAlphanumeric(12));
        squad.setName(RandomStringUtils.randomAlphabetic(20));
        squadDao.save(squad);
        return squad;
    }

    void handleMemberRequest(Squad squad, SquadifyUser member, SquadMemberAction action) {
        action.apply(squad, member);
        squadDao.save(squad);
    }

    void renameSquad(Squad squad, String newName) {
        squad.setName(newName);
        squadDao.save(squad);
    }

}
