package com.cttexpress.models;

import java.util.ArrayList;
import java.util.Arrays;

public class ErrorResponseFactory {

    public static ErrorResponse getInstance(String errorId, String errorAlias, String singleErrorMessage) {

        ErrorResponse instance = new ErrorResponse();
        instance.errorId = errorId;
        instance.errorAlias = errorAlias;
        instance.errorMessages = new ArrayList<String>(
                Arrays.asList(singleErrorMessage));
        return instance;
    }

    public static ErrorResponse getInstance(String errorId, String errorAlias, ArrayList<String> errorMessages) {

        ErrorResponse instance = new ErrorResponse();
        instance.errorId = errorId;
        instance.errorAlias = errorAlias;
        instance.errorMessages = errorMessages;
        return instance;
    }

}
