package com.squadify.app.member;

import com.squadify.app.squad.Squad;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final SquadDao squadDao;
    private final SquadifyUserDao squadifyUserDao;

    public void addSquadMember(String squadId, String username) {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        SquadifyUser member = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squad.getRequests().remove(member);
        squad.getMembers().add(member);
        squadDao.save(squad);
    }

    public void removeSquadMember(String squadId, String username) {
        Squad squad = squadDao.findBySquadId(squadId).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        SquadifyUser member = squadifyUserDao.findByUsername(username).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND));
        squad.removeMember(member);
        squadDao.save(squad);
    }
}
