package com.pc.ecom.Exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResourceNotFoundException extends RuntimeException{
    String message;
    String resource;
    String field;
    Long fieldId;
    public ResourceNotFoundException(String resource,Long fieldId, String field) {
        super("Not Found");
        this.resource = resource;
        this.field = field;
        this.fieldId = fieldId;
        this.message = String.format("Resource %s, Field %s not found", resource,field);
    }
}
