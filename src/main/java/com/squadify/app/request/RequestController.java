package com.squadify.app.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/squads/{squadId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PutMapping
    public void addRequest(@PathVariable String squadId) {
        requestService.addRequest(squadId);
    }

    @DeleteMapping
    public void deleteRequest(@PathVariable String squadId, @PathVariable String username) {
        requestService.deleteRequest(squadId, username);
    }

}
