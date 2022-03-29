package com.squadify.app.user;

import com.squadify.app.auth.UnauthorisedException;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.dto.SquadifyUserResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import static com.squadify.app.SquadifyApiConfig.BASE_URL;

@RestController
@RequestMapping("user")
@CrossOrigin(origins = BASE_URL + ":3000", allowCredentials = "true")
public class SquadifyUserController extends SquadifyController {

    private final SquadifyUserService squadifyUserService;

    public SquadifyUserController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, SquadifyUserService squadifyUserService) {
        super(squadDao, squadifyUserDao);
        this.squadifyUserService = squadifyUserService;
    }

    @GetMapping
    @CrossOrigin(origins = BASE_URL + ":3000")
    public SquadifyUserResponse getUser(HttpSession session) throws UnauthorisedException {
        String userId = (String) session.getAttribute("userId");
        SquadifyUser squadifyUser = squadifyUserDao.findByUsername(userId).orElseThrow(() -> new UnauthorisedException("/"));
        return squadifyUserService.mapToSquadifyUserResponse(squadifyUser);
    }

}
