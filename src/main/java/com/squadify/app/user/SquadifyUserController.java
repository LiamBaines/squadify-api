package com.squadify.app.user;

import com.squadify.app.auth.UnauthorisedException;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.model.SquadifyUserAndSquadsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class SquadifyUserController extends SquadifyController {

    private final SquadifyUserService squadifyUserService;

    public SquadifyUserController(SquadifyUserService squadifyUserService) {
        super(null, null);
        this.squadifyUserService = squadifyUserService;
    }

    @GetMapping
    public SquadifyUserAndSquadsDto getUser() throws UnauthorisedException {
        return squadifyUserService.getSquadifyUserAndSquads();
    }

}
