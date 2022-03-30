package com.squadify.app.auth;

import com.squadify.app.SquadifyApiConfig;
import com.squadify.app.user.SquadifyUser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SquadifyApiConfig config;

    @GetMapping("callback")
    public String callback(@RequestParam String code, @RequestParam String state, HttpSession session) throws ParseException, SpotifyWebApiException, IOException {
        SquadifyUser squadifyUser = authService.getSquadifyUserFromCode(code, config.getRedirectUrl());
        session.setAttribute("userId", squadifyUser.getUsername());
        return "redirect:" + config.getClientUrl() + state;
    }

}