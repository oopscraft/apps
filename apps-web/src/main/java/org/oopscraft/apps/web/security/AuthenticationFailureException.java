package org.oopscraft.apps.web.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AuthenticationFailureException extends RuntimeException {

    public AuthenticationFailureException(String message){
        super(message);
    }
    
}
