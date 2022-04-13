package com.squadify.app.squad;

import com.squadify.app.model.SquadDto;
import com.squadify.app.model.UpdateSquadRequestDto;
import com.squadify.app.playlist.Playlist;
import com.squadify.app.playlist.PlaylistDao;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SquadService {

    private final HttpSession session;
    private final PlaylistDao playlistDao;
    private final SquadDao squadDao;
    private final SquadifyUserDao squadifyUserDao;
    private final SquadResponseMapper squadResponseMapper;

    SquadDto createSquad() {
        String username = (String) session.getAttribute("username");
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        Squad squad = new Squad();
        squad.setOwner(squadifyUser);
        squad.setSquadId(RandomStringUtils.randomAlphanumeric(12));
        squad.setName(RandomStringUtils.randomAlphabetic(20));
        squadDao.save(squad);
        return squadResponseMapper.map(squad);
    }

    public void updateSquad(String squadId, UpdateSquadRequestDto request) {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squad.setName(request.getName());
        squadDao.save(squad);
    }

    public void deleteSquad(String squadId) {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squadDao.delete(squad);
    }

    public void addPlaylistUrlToSquad(Squad squad, String playlistUrl) {
        Playlist playlist = new Playlist().setUrl(playlistUrl);
        playlistDao.save(playlist);
        squad.setPlaylist(playlist);
        squadDao.save(squad);
    }
}
