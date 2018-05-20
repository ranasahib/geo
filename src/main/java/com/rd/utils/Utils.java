/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author abhishek
 */
public class Utils {
    public static Map<String,Boolean> getMenu(List<String> roles){
        Map<String,Boolean> roleMap = new HashMap<String,Boolean>();
        for(String role:roles){
            if(role.equals("RegistryAdministrator")){
                roleMap.put("ListOrganisation", true);
                roleMap.put("CreateOrganisation", true);
                roleMap.put("roles", true);
                roleMap.put("users", true);   
                }
            if(role.equals("RegistryUser")){
                roleMap.put("ListOrganisation", true);
                roleMap.put("CreateOrganisation", true);
                roleMap.put("roles", true);  
            }
            if(role.equals("NodalOfficer")){
                roleMap.put("ListUser", true);
                roleMap.put("ApproveUser", true);
            }
            if(role.equals("Admin")){
                roleMap.put("ListUser", true);
                roleMap.put("SubmitUser", true);
                roleMap.put("CreateUser", true);
            }
            if(role.equals("OrganisationUser")){
                roleMap.put("MapLayer", true);
                roleMap.put("Harvest", true);
            }
        }
        return roleMap;
    }
}
