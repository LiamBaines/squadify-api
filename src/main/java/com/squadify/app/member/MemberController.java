package com.squadify.app.member;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/squads/{squadId}/members/{username}")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PutMapping
    public void addSquadMember(@PathVariable String squadId, @PathVariable String username) {
        memberService.addSquadMember(squadId, username);
    }

    @DeleteMapping
    public void removeSquadMember(@PathVariable String squadId, @PathVariable String username) {
        memberService.removeSquadMember(squadId, username);
    }

}
