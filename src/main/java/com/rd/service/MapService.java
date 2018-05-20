/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rd.service;

import java.util.List;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.json.simple.JSONObject;

import com.rd.dto.ResponseWrapper;

/**
 *
 * @author abhishek
 */
public interface MapService {

//	List<String> fetchCapabilities();

	ResponseWrapper mapView(JSONObject o);

	JSONObject fetchCapabilities(String uri);
  
}
