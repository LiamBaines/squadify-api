package com.squadify.app.request;

import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpSession;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final HttpSession session;
    private final SquadDao squadDao;
    private final SquadifyUserDao squadifyUserDao;

    public void addRequest(String squadId) {
        String username = (String) session.getAttribute("username");
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squad.addRequest(squadifyUser);
    }

    public void deleteRequest(String squadId, String username) {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squad.removeRequest(squadifyUser);
    }
}
