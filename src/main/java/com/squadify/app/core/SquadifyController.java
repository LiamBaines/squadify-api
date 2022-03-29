package com.squadify.app.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squadify.app.auth.UnauthorisedException;
import com.squadify.app.squad.SquadDao;
import com.squadify.app.user.SquadifyUserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
public class SquadifyController {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected final SquadDao squadDao;
    protected final SquadifyUserDao squadifyUserDao;

    @ResponseBody
    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorisedException e) {
        return ResponseEntity.status(UNAUTHORIZED).body(e.getMessage());
    }

}
