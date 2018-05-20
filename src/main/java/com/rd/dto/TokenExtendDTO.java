/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.dto;

import java.util.List;
import java.util.Map;

/**
 *
 * @author abhishek
 */
public class TokenExtendDTO {
    private String token;
    private List<String> roles;
    private Map<String,Boolean> menus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Map<String, Boolean> getMenus() {
        return menus;
    }

    public void setMenus(Map<String, Boolean> menus) {
        this.menus = menus;
    }
    
    
}
