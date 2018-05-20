package com.rd.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.ows.ServiceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.rd.dto.ResponseWrapper;
/**
*
* @author abhishek
*/
@Service
public class MapServiceImpl implements MapService{

	@Override
	public JSONObject fetchCapabilities(String uri){
		URL url = null;
//		uri = 	"http://localhost:8080/geoserver/demo/ows?service=WMS&";
		WMSCapabilities capabilities = null;
		if(null == uri || uri.trim().isEmpty()){
			uri = 	"http://103.62.95.146:8090/geoserver/cite/ows?service=WMS&";
		}
		try {
		  url = new URL(uri +"version=1.1.0&request=GetCapabilities");
		} catch (MalformedURLException e) {
		  //will not happen
		}

		WebMapServer wms = null;
		JSONObject response = new JSONObject();
		JSONArray layerList = new JSONArray();
//		JSONArray bbox = new JSONArray();
		
//		List<String> layerList = new ArrayList<String>();
		try {
		  wms = new WebMapServer(url);
		  capabilities = wms.getCapabilities();
		  org.geotools.data.ows.Layer[] list = WMSUtils.getNamedLayers(capabilities);
		  if(null != list && list.length>0){		  
			  for ( org.geotools.data.ows.Layer o : list ) {
				  layerList.add(o.getName());
				}
			  double[] center= list[0].getEnvelope(list[0].getLatLonBoundingBox()
					  .getCoordinateReferenceSystem()).getMedian().getCoordinate();
		
				response.put("layers", layerList);
				response.put("bbox", center);
		  }
		  
		} catch (IOException e) {
		  //There was an error communicating with the server
		  //For example, the server is down
		} catch (ServiceException e) {
		  //The server returned a ServiceException (unusual in this case)
		} catch (SAXException e) {
		  //Unable to parse the response from the server
		  //For example, the capabilities it returned was not valid
		}
		
		
		return response;
	}

	@Override
	public ResponseWrapper mapView(JSONObject request) {
		ResponseWrapper responseWrapper =  null;
		JSONObject layers = fetchCapabilities(null!=request?(String)request.get("accessUrl"):null);
		if(null == layers){
			responseWrapper = new ResponseWrapper(false, "Something went wrong");
		}else{
			
			responseWrapper = new ResponseWrapper(layers);
		}
		return responseWrapper;
	}
	
}