/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto.transaction;

import java.util.List;

/**
 *
 * @author abhishek
 */
public class InsertResult {
    
    private List<BriefRecord> briefRecords;

    public List<BriefRecord> getBriefRecords() {
        return briefRecords;
    }

    public void setBriefRecords(List<BriefRecord> briefRecords) {
        this.briefRecords = briefRecords;
    }
    
}
