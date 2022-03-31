package com.squadify.app.auth;

import com.squadify.app.squad.Squad;
import com.squadify.app.user.SquadifyUser;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
@Deprecated
public class RequestContext {
    private Squad squad;
    private SquadifyUser user;
}
