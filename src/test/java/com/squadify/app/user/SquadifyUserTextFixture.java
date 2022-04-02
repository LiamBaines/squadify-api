package com.squadify.app.user;

import lombok.experimental.UtilityClass;

import static com.squadify.app.core.SquadifyTestFixture.someString;

@UtilityClass
public class SquadifyUserTextFixture {

    public static SquadifyUser someSquadifyUser() {
        return someSquadifyUser(someString());
    }

    public static SquadifyUser someSquadifyUser(String username) {
        SquadifyUser squadifyUser = new SquadifyUser();
        squadifyUser.setUsername(username);
        squadifyUser.setName(username);
        return squadifyUser;
    }

}
