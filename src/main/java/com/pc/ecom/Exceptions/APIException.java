package com.pc.ecom.Exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class APIException extends RuntimeException {
    String message;
    public APIException(String message) {
        this.message = message;
    }
}
