package com.video.processing.utilities;

public class ResponseFromApi<T> {
    private String message;
    private Integer statusCode;
    private T payload;

    public ResponseFromApi(){}

    public ResponseFromApi(String message, int statusCode, T payload){
        this.message = message;
        this.statusCode = statusCode;
        this.payload = payload;
    }

    public String getMessage(){
        return this.message;
    }

    public void setMessage(String message){
        this.message = message;
    }
    
    public int getStatusCode(){
        return this.statusCode;
    }

    public void setStatusCode(Integer statusCode){
        this.statusCode = statusCode;
    }

    public T getPayload(){
        return this.payload;
    }

    public void setPayload(T payload){
        this.payload = payload;
    }

    public static <T> ResponseFromApi<T> success(T data, String message) {
        return new ResponseFromApi<>(message, 200, data);
    }

    public static <T> ResponseFromApi<T> error(String message, int statusCode) {
        return new ResponseFromApi<>(message, statusCode, null);
    }
}
