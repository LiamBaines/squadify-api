package com.squadify.app.auth;

import lombok.Getter;

public class UnauthorisedException extends Exception {

    @Getter
    private final String redirect;

    public UnauthorisedException(String redirect) {
        super();
        this.redirect = redirect;
    }

}
