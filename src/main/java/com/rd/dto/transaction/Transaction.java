package com.rd.dto.transaction;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.List;
import java.util.Map;

/**
 *
 * @author abhishek
 */
public class Transaction {
    List<Map<String,Object>> transaction;

    public List<Map<String, Object>> getTransaction() {
        return transaction;
    }

    public void setTransaction(List<Map<String, Object>> transaction) {
        this.transaction = transaction;
    }
    
    
}
