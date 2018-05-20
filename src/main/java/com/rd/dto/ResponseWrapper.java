/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 * @author abhishek
 */
@JsonInclude(Include.NON_NULL)
public class ResponseWrapper {
    
    private boolean success;
    private String message;
    private Object data;
    private String code;

    public ResponseWrapper(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseWrapper(Object data) {
        this.success = true;
        this.data = data;
    }
    
    

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
