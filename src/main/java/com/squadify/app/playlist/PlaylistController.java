package com.squadify.app.playlist;

import com.squadify.app.auth.RequestContext;
import com.squadify.app.auth.SquadOwnerOnly;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.playlist.dto.PlaylistResponse;
import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUserDao;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("squads/{squadKey}/playlist")
public class PlaylistController extends SquadifyController {

    private final PlaylistCreator playlistCreator;

    @Autowired
    private RequestContext requestContext;

    @Autowired
    public PlaylistController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, PlaylistCreator playlistCreator) {
        super(squadDao, squadifyUserDao);
        this.playlistCreator = playlistCreator;
    }

    @PostMapping
    @SquadOwnerOnly
    public PlaylistResponse createSquadPlaylist(@PathVariable String squadKey) throws ParseException, SpotifyWebApiException, IOException {
        Squad squad = squadDao.findBySquadKey(squadKey).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        String url = playlistCreator.createPlaylist(squad).getId();
        return new PlaylistResponse(url);
    }

}
