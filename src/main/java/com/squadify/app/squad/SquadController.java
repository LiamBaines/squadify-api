package com.squadify.app.squad;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.squadify.app.auth.RequestContext;
import com.squadify.app.auth.SquadOwnerOnly;
import com.squadify.app.auth.UnauthorisedException;
import com.squadify.app.core.SquadifyController;
import com.squadify.app.squad.dto.RenameSquadRequest;
import com.squadify.app.squad.dto.SquadMemberActionRequest;
import com.squadify.app.squad.dto.SquadResponse;
import com.squadify.app.user.SquadifyUser;
import com.squadify.app.user.SquadifyUserDao;
import com.wrapper.spotify.exceptions.detailed.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

import static com.squadify.app.squad.dto.SquadMemberAction.JOIN;
import static com.squadify.app.squad.dto.SquadResponseMapper.mapToSquadResponse;

@RestController
@RequestMapping("squads")
public class SquadController extends SquadifyController {

    private final SquadService squadService;
    private final HttpSession session;

    @Autowired
    private RequestContext requestContext;

    @Autowired
    public SquadController(SquadDao squadDao, SquadifyUserDao squadifyUserDao, SquadService squadService, HttpSession session) {
        super(squadDao, squadifyUserDao);
        this.squadService = squadService;
        this.session = session;
    }

    @PostMapping
    public SquadResponse createSquad() throws BadRequestException {
        // to verify: user exists
        Squad squad = squadService.createSquad(requestContext.getUser());
        return mapToSquadResponse(squad);
    }

    @GetMapping("{squadKey}/join")
    public void joinSquad(@PathVariable String squadKey) throws UnauthorisedException {
        // to verify: user exists, squad exists
        String userId = Optional.ofNullable((String) session.getAttribute("userId")).orElseThrow(() -> new UnauthorisedException("/"));
        SquadifyUser user = squadifyUserDao.findByUsername(userId).orElseThrow(() -> new UnauthorisedException("User doesn't exist"));
        squadService.handleMemberRequest(requestContext.getSquad(), user, JOIN);
    }

    @PostMapping("{squadKey}/members")
    @SquadOwnerOnly
    public void manageSquadMembers(@PathVariable String squadKey, @RequestBody String json, HttpSession session) throws JsonProcessingException, UnauthorisedException {
        // to verify: user exists, squad exists, user owns squad
        SquadMemberActionRequest request = objectMapper.readValue(json, SquadMemberActionRequest.class);
        SquadifyUser user = squadifyUserDao.findByUsername(request.getUsername()).orElseThrow(() -> new UnauthorisedException("User doesn't exist"));
        squadService.handleMemberRequest(requestContext.getSquad(), user, request.getAction());
    }

    @DeleteMapping("{squadKey}")
    @SquadOwnerOnly
    public void deleteSquad(@PathVariable String squadKey) {
        // to verify: squad exists, user owns squad
        squadDao.deleteBySquadKey(squadKey);
    }

    @PostMapping("{squadKey}/rename")
    @SquadOwnerOnly
    public void renameSquad(@RequestBody String json) throws JsonProcessingException {
        // to verify: user exists, squad exists, user owns squad
        RenameSquadRequest request = objectMapper.readValue(json, RenameSquadRequest.class);
        squadService.renameSquad(requestContext.getSquad(), request.getNewName());
    }

}
