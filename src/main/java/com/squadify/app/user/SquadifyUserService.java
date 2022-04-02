package com.squadify.app.user;

import com.squadify.app.model.SquadifyUserAndSquadsDto;
import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SquadifyUserService {

    private final HttpSession session;
    private final SquadDao squadDao;
    private final SquadifyUserDao squadifyUserDao;
    private final SquadifyUserAndSquadsResponseMapper squadifyUserAndSquadsResponseMapper;

    public SquadifyUserAndSquadsDto getSquadifyUserAndSquads() {
        String username = (String) session.getAttribute("username");
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        List<Squad> squads = squadDao.findByOwnerOrMembersContainsOrRequestsContains(squadifyUser);
        return squadifyUserAndSquadsResponseMapper.map(squadifyUser, squads);
    }
}
