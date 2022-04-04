package com.squadify.app.squad;

import com.squadify.app.user.SquadifyUser;
import lombok.experimental.UtilityClass;

import java.util.stream.IntStream;

import static com.squadify.app.core.SquadifyTestFixture.someString;
import static com.squadify.app.user.SquadifyUserTextFixture.someSquadifyUser;

@UtilityClass
public class SquadFixture {

    public static Squad someSquad(int size) {
        Squad squad = new Squad();
        squad.setSquadId(someString());
        squad.setName(someString());
        IntStream.range(1, size).forEach(i -> addMember(squad));
        squad.setOwner(someSquadifyUser());

        return squad;
    }

    private static void addMember(Squad squad) {
        SquadifyUser user = someSquadifyUser();
        squad.addMemberRequest(user);
        squad.acceptMember(user);
    }

}
