package com.rd.utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

public class FilterExample {
	public static void main(String[] args) throws IOException, CQLException {
		ClassLoader classLoader = FilterExample.class.getClassLoader();
		URL fileUrl = new File("/home/abhishek/Desktop/panacea/Road/Road.shp").toURI().toURL();
		Map<String, Serializable> map = new HashMap<>();
		map.put("url", fileUrl);
		DataStore dataStore = DataStoreFinder.getDataStore(map);

		Filter filter = ECQL.toFilter("CATEGORY = 'NH'");

		String typeName = dataStore.getTypeNames()[0];
		SimpleFeatureSource simpleFeatureSource = dataStore.getFeatureSource(typeName);
		SimpleFeatureCollection result = simpleFeatureSource.getFeatures(filter);
		
		SimpleFeatureIterator featureIterator = result.features();
		
		while(featureIterator.hasNext()){			
		    SimpleFeature feature = featureIterator.next();		 
		    System.out.println(feature.getAttribute("NAME"));
		}

	}
}
