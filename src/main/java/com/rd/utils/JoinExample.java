package com.rd.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This class shows how to "join" two feature sources.
 * 
 * @author Jody
 * 
 * 
 * @source $URL$
 */
public class JoinExample {

	/**
	 * 
	 * @param args
	 *            shapefile to use, if not provided the user will be prompted
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to GeoTools:" + GeoTools.getVersion());

//		File file, file2;
//		if (args.length == 0) {
//			file = JFileDataStoreChooser.showOpenFile("shp", null);
//		} else {
//			file = new File(args[0]);
//		}
//		if (args.length <= 1) {
//			file2 = JFileDataStoreChooser.showOpenFile("shp", null);
//		} else {
//			file2 = new File(args[1]);
//		}
//		if (file == null || !file.exists() || file2 == null || !file2.exists()) {
//			System.exit(1);
//		}
//		
//		System.out.println(file.toURI().toURL());
//		System.out.println(file.toURI().toURL());
//		
		Map<String, Object> connect = new HashMap<>();
		connect.put("url", "file:/home/abhishek/Desktop/panacea/Shapefiles/RiverBank/River%20Bank.shp");
		DataStore shapefile = DataStoreFinder.getDataStore(connect);
		String typeName = shapefile.getTypeNames()[0];
		SimpleFeatureSource shapes = shapefile.getFeatureSource(typeName);

		Map<String, Object> connect2 = new HashMap<>();
		connect2.put("url", "file:/home/abhishek/Desktop/panacea/Shapefiles/RiverFlatSand/River%20Flat%20Sand.shp");
		DataStore shapefile2 = DataStoreFinder.getDataStore(connect2);
		String typeName2 = shapefile2.getTypeNames()[0];
		SimpleFeatureSource shapes2 = shapefile2.getFeatureSource(typeName2);

		joinExample(shapes, shapes2);
		System.exit(0);
	}

	// joinExample start
	private static void joinExample(SimpleFeatureSource shapes, SimpleFeatureSource shapes2) throws Exception {
		SimpleFeatureType schema = shapes.getSchema();
		String typeName = schema.getTypeName();
		String geomName = schema.getGeometryDescriptor().getLocalName();

		SimpleFeatureType schema2 = shapes2.getSchema();
		String typeName2 = schema2.getTypeName();
		String geomName2 = schema2.getGeometryDescriptor().getLocalName();
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Query outerGeometry = new Query(typeName, Filter.INCLUDE, new String[] { geomName });
		SimpleFeatureCollection outerFeatures = shapes.getFeatures(outerGeometry);
		SimpleFeatureIterator iterator = outerFeatures.features();
		int max = 0;
		try {
			while (iterator.hasNext()) {
				SimpleFeature feature = iterator.next();
				try {
					Geometry geometry = (Geometry) feature.getDefaultGeometry();
					if (!geometry.isValid()) {
						// skip bad data
						continue;
					}
					Filter innerFilter = ff.intersects(ff.property(geomName2), ff.literal(geometry));
					Query innerQuery = new Query(typeName2, innerFilter, Query.NO_NAMES);
					SimpleFeatureCollection join = shapes2.getFeatures(innerQuery);
					
					FeatureJSON geoJson = new FeatureJSON();
				    System.out.println( "Connected to  with " + geoJson.toString(join) );

					int size = join.size();
					max = Math.max(max, size);
				} catch (Exception skipBadData) {
				}
			}
		} finally {
			iterator.close();
		}
		System.out.println("At most " + max + " " + typeName2 + " features in a single " + typeName + " feature");
	}
	// joinExample end

	// Filter innerFilter = ff.dwithin(ff.property(geomName2), ff.literal(
	// geometry
	// ),1.0,"km");
	// Filter innerFilter = ff.beyond(ff.property(geomName2), ff.literal(
	// geometry
	// ),1.0,"km");
	// Filter innerFilter = ff.not( ff.disjoint(ff.property(geomName2),
	// ff.literal(
	// geometry )) );

}