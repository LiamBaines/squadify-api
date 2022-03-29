package com.squadify.app.auth;

import com.squadify.app.SquadifyApiConfig;
import com.squadify.app.user.SquadifyUser;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.squadify.app.api.SpotifyApiFactory.REDIRECT_URI;

@Controller
@RequestMapping("auth")
@RequiredArgsConstructor
@CrossOrigin(origins = SquadifyApiConfig.BASE_URL + ":3000")
public class AuthController {

    private final AuthService authService;

    @GetMapping("callback")
    @CrossOrigin(SquadifyApiConfig.BASE_URL + ":3000")
    public String callback(@RequestParam String code, @RequestParam String state, HttpSession session) throws ParseException, SpotifyWebApiException, IOException {
        SquadifyUser squadifyUser = authService.getSquadifyUserFromCode(code, REDIRECT_URI);
        session.setAttribute("userId", squadifyUser.getUsername());
        return "redirect:" + SquadifyApiConfig.BASE_URL + ":3000" + state;
    }

}