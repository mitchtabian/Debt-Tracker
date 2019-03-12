package com.codingwithmitch.debttracker.util;

public class CursorResponse<T>  {

    public CursorResponse<T> create(Throwable error){
        return new CursorErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error");
    }

    public CursorResponse<T> create(T data){

        if(data != null){
            return new CursorSuccessResponse<>(data);
        }
        return new CursorErrorResponse<>("No data");
    }

    /**
     * Generic success response from cursor
     * @param <T>
     */
    public class CursorSuccessResponse<T> extends CursorResponse<T> {

        private T body;

        CursorSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    /**
     * Generic success response from cursor
     * @param <T>
     */
    public class CursorErrorResponse<T> extends CursorResponse<T> {

        private String errorMessage;

        CursorErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }

}
