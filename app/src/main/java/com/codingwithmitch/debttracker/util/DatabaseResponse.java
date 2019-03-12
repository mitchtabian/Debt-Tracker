package com.codingwithmitch.debttracker.util;

public class DatabaseResponse<T> {

    public DatabaseResponse<T> create(Throwable error){
        return new DatabaseErrorResponse<>(error.getMessage().equals("") ? error.getMessage() : "Unknown error");
    }

    public DatabaseResponse<T> create(T data){

        if(data != null){
            return new DatabaseSuccessResponse<>(data);
        }
        return new DatabaseErrorResponse<>("No data");
    }

    /**
     * Generic success response from local db
     * @param <T>
     */
    public class DatabaseSuccessResponse<T> extends DatabaseResponse<T> {

        private T body;

        DatabaseSuccessResponse(T body) {
            this.body = body;
        }

        public T getBody() {
            return body;
        }

    }

    /**
     * Generic success response from local db
     * @param <T>
     */
    public class DatabaseErrorResponse<T> extends DatabaseResponse<T> {

        private String errorMessage;

        DatabaseErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

    }
}
