/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto.transaction;

import java.util.List;
import java.util.Map;

/**
 *
 * @author abhishek
 */
public class Response {
    private Map<String,String> transactionSummary;
    private List<InsertResult> insertResults;

    public Map<String, String> getTransactionSummary() {
        return transactionSummary;
    }

    public void setTransactionSummary(Map<String, String> transactionSummary) {
        this.transactionSummary = transactionSummary;
    }

    public List<InsertResult> getInsertResults() {
        return insertResults;
    }

    public void setInsertResults(List<InsertResult> insertResults) {
        this.insertResults = insertResults;
    }
    
}
