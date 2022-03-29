package com.squadify.app.squad.dto;

import com.squadify.app.squad.Squad;
import com.squadify.app.user.SquadifyUser;
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public enum SquadMemberAction {

    ACCEPT(Squad::acceptMember),
    DECLINE(Squad::declineMember),
    JOIN(Squad::addMemberRequest),
    REMOVE(Squad::removeMember);

    private final BiConsumer<Squad, SquadifyUser> function;

    public void apply(Squad squad, SquadifyUser user) {
        function.accept(squad, user);
    }

}
