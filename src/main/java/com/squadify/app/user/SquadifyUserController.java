package com.squadify.app.user;

import com.squadify.app.auth.UnauthorisedException;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.dto.SquadifyUserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class SquadifyUserController extends SquadifyController {

    private final SquadifyUserService squadifyUserService;

    public SquadifyUserController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, SquadifyUserService squadifyUserService) {
        super(squadDao, squadifyUserDao);
        this.squadifyUserService = squadifyUserService;
    }

    @GetMapping
    public SquadifyUserResponse getUser(HttpSession session) throws UnauthorisedException {
        String userId = (String) session.getAttribute("userId");
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(userId).orElseThrow(() -> new UnauthorisedException("/"));
        return squadifyUserService.mapToSquadifyUserResponse(squadifyUser);
    }

}
