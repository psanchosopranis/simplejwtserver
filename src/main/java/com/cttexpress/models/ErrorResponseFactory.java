package com.cttexpress.models;

public class ErrorResponseFactory {

    public static ErrorResponse getInstance(String errorId, String errorAlias, String errorMessage) {

        ErrorResponse instance = new ErrorResponse();
        instance.errorId = errorId;
        instance.errorAlias = errorAlias;
        instance.errorMessage = errorMessage;
        return instance;
    }

}
