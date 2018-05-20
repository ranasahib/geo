package com.rd.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FunctionFactory;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cql.extension.QueryLayerFunctionFactory;
import com.rd.dto.ComplexQueryDTO;
import com.rd.dto.ResponseWrapper;
import com.rd.dto.UserDetailDTO;

@Service
public class ComplexQueryServiceImpl {

	@Autowired
	DataStore dataStore;
	
	FeatureJSON geoJson = new FeatureJSON();

	public static void main(String[] args) throws IOException, CQLException {
		ComplexQueryServiceImpl o = new ComplexQueryServiceImpl();
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("dbtype", "postgis");
			params.put("host", "localhost");
			params.put("port", 5432);
			params.put("schema", "public");
			params.put("database", "postgres");
			params.put("user", "postgres");
			params.put("passwd", "postgres");

			o.dataStore = DataStoreFinder.getDataStore(params);
			String[] typeName = o.dataStore.getTypeNames(); // should be "example"
			List<String> queries = new ArrayList<String>();
			queries.add("fclass='railway_halt' and code='5602'");
			queries.add("fclass='railway_halt'");
//			queries.add("name='Girdharpur'");
//			queries.add("DWITHIN(geom, collectGeometries(queryCollection('test_canalsline','geom','INCLUDE')), 6, kilometers)");
//			queries.add("DWITHIN(geom, collectGeometries(queryCollection('test_canalsline','geom','INCLUDE')), 6, kilometers)");
			System.out.println(o.getFetaures(typeName[0], null, queries, "OR"));
			System.out.println(o.getFetaures(typeName[0], null, queries, "AND"));
			
//			FeatureType schema = dataStore.getSchema(typeName[0]); // should be
//			QueryLayerFunctionFactory factory = null; // "refractions.example"
//
//			Set<FunctionFactory> factories = CommonFactoryFinder.getFunctionFactories(null);
//			for (FunctionFactory ff : factories) {
//				if ((ff instanceof QueryLayerFunctionFactory)) {
//					factory = (QueryLayerFunctionFactory) ff;
//
//					factory.setCatalog(dataStore);
//				}
//			}
//			Filter filter1 = ECQL.toFilter(
//					"DWITHIN(geom, collectGeometries(queryCollection('test_canalsline','geom','INCLUDE')), 6, kilometers)");
//			Filter filter2 = ECQL.toFilter(
//					"DWITHIN(geom, collectGeometries(queryCollection('test_canalsline','geom','INCLUDE')), 8, kilometers)");

			// String exampleString = "<ogc:Filter
			// xmlns:ogc=\"http://www.opengis.net/ogc\"
			// xmlns:gml=\"http://www.opengis.net/gml\"><ogc:DWithin><ogc:PropertyName>the_geom</ogc:PropertyName><ogc:Function
			// name=\"collectGeometries\"><ogc:Function
			// name=\"queryCollection\"><ogc:Literal>sf:roads</ogc:Literal><ogc:Literal>the_geom</ogc:Literal><ogc:Literal>INCLUDE</ogc:Literal></ogc:Function></ogc:Function><ogc:Distance
			// units=\"meter\">100</ogc:Distance></ogc:DWithin></ogc:Filter>";
			// InputStream inputStream = new
			// ByteArrayInputStream(exampleString.getBytes(StandardCharsets.UTF_8.name()));
			// Configuration configuration = new
			// org.geotools.filter.v1_0.OGCConfiguration();
			// Parser parser = new Parser( configuration );
			// Filter filter = (Filter) parser.parse( inputStream );

//			Query query1 = new Query(typeName[0], filter1);
//			Query query2 = new Query(typeName[0], filter2);
//			SimpleFeatureSource contents = dataStore.getFeatureSource(typeName[0]);
//			SimpleFeatureCollection features1 = contents.getFeatures(query1);
//			SimpleFeatureCollection features2 = contents.getFeatures(query2);
//			List<SimpleFeatureCollection> faturesList = new ArrayList<SimpleFeatureCollection>();
//			faturesList.add(features1);
//			faturesList.add(features2);
			// SimpleFeatureCollection features2 = contents.getFeatures(query);
			// if(features instanceof ContentFeatureCollection){
			// boolean chk =
			// ((ContentFeatureCollection)features).contains(((ContentFeatureCollection)features2).features().next());
			// System.out.println(chk);
			//
			// }
			// FeatureIterator<SimpleFeature> iterator = features.features();
			// int tot = 0;
			// boolean add = false;
			// List<SimpleFeature> commonFetaureList = new
			// ArrayList<SimpleFeature>();
			// try {
			// while( iterator.hasNext()){
			// SimpleFeature feature = iterator.next();
			// if(((ContentFeatureCollection)features).contains(feature)){
			// tot+=1;
			// if(add){
			// commonFetaureList.add(feature);
			// add = false;
			// }else{
			// add = true;
			// }
			// }
			// }
			// }
			// finally{
			// iterator.close();
			// }
			// System.out.println("Total : "+tot);
			// System.out.println("Common : "+commonFetaureList.size());
			// SimpleFeatureCollection common =
			// DataUtilities.collection(commonFetaureList);
			// CRS.decode("EPSG:4326").equals(features.getSchema().getCoordinateReferenceSystem());

//			FeatureJSON geoJson = new FeatureJSON();
//			System.out.println("Connected to  with " + geoJson.toString(commonFeatures(faturesList)));
//			System.out.println("-------------------------------------------------------------------");
//			System.out.println("Connected to  with " + geoJson.toString(allFeatures(faturesList)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// catch (FactoryException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } catch (SAXException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ParserConfigurationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}
	
	
	public ResponseWrapper complexQuery(ComplexQueryDTO queryData, UserDetailDTO userData) {
		if(null == queryData 
				|| queryData.getQueryLayerName() == null || queryData.getQueryLayerName().isEmpty() 
				|| queryData.getQueries() ==null || queryData.getQueries().isEmpty()){
			return new ResponseWrapper(false, "NULL PARAMS");
		}
		
		String response = getFetaures(queryData.getQueryLayerName(), queryData.getRefLayers(), queryData.getQueries(), queryData.getJoinOperationType());
		
		if(null == response || response.isEmpty()){
			return new ResponseWrapper(false,"NO RECORD FOUND");
		}else{
			return new ResponseWrapper( response);
		}
		
		
	}

	public String getFetaures(String queryLayerName, List<String> refLayers, List<String> queries,
			String operation) {
		String response = "";
		if (null != queryLayerName && !queryLayerName.isEmpty() && null != queries && !queries.isEmpty()) {

			try {

				String[] typeName = dataStore.getTypeNames();
				QueryLayerFunctionFactory factory = null;
				Set<FunctionFactory> factories = CommonFactoryFinder.getFunctionFactories(null);
				for (FunctionFactory ff : factories) {
					if ((ff instanceof QueryLayerFunctionFactory)) {
						factory = (QueryLayerFunctionFactory) ff;

						factory.setCatalog(dataStore);
					}
				}
				List<SimpleFeatureCollection> faturesList = new ArrayList<SimpleFeatureCollection>();
				for (String queryString : queries) {
					Filter filter;
					try {
						filter = ECQL.toFilter(queryString.trim());
						Query query = new Query(typeName[0], filter);
						SimpleFeatureSource contents = dataStore.getFeatureSource(queryLayerName);
						SimpleFeatureCollection features = contents.getFeatures(query);
						faturesList.add(features);
					} catch (CQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				if (null != faturesList && !faturesList.isEmpty()) {
					if (null != operation && operation.equalsIgnoreCase("or")) {
						response = geoJson.toString(allFeatures(faturesList));
					} else {
						response = geoJson.toString(commonFeatures(faturesList));
						;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return response;

	}

	public static SimpleFeatureCollection commonFeatures(List<SimpleFeatureCollection> list) {
		List<SimpleFeature> commonFetaureList = null;
		SimpleFeatureCollection common = list.get(0);
		for (int i = 1; i < list.size(); i++) {
			SimpleFeatureCollection features = list.get(i);
			FeatureIterator<SimpleFeature> iterator = common.features();
			commonFetaureList = new ArrayList<SimpleFeature>();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					if (features.contains(feature)) {

						commonFetaureList.add(feature);

					}
				}
			} finally {
				iterator.close();
			}
			if (null == commonFetaureList || commonFetaureList.isEmpty()) {
				break;
			}
			common = DataUtilities.collection(commonFetaureList);

		}

		return common;
	}

	public static SimpleFeatureCollection allFeatures(List<SimpleFeatureCollection> list) {
		List<SimpleFeature> commonFetaureList = new ArrayList<SimpleFeature>();
		for (int i = 1; i < list.size(); i++) {
			SimpleFeatureCollection features = list.get(i);
			FeatureIterator<SimpleFeature> iterator = features.features();

			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					if (!(commonFetaureList).contains(feature)) {

						commonFetaureList.add(feature);

					}
				}
			} finally {
				iterator.close();
			}

		}

		return DataUtilities.collection(commonFetaureList);
	}

}
