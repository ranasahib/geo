//package com.rd.utils;
//
//
//import java.awt.Rectangle;
//
//import org.geotools.map.Layer;
//import org.geotools.map.MapContent;
//import org.opengis.feature.type.FeatureType;
//import org.opengis.geometry.primitive.Point;
//
//public class JoinMultipleLayer {
//	public static void main(String[] args) {
//		MapContent con = mapPane.getMapContent();
//		con.layers();
//		for (Layer l : con.layers()) {
//			FeatureType schema = l.getFeatureSource().getSchema();
//			String geometryPropertyName = schema.getGeometryDescriptor().getLocalName(); // "THE_GEOM"
//			System.out.println("Layer : " + geometryPropertyName);
//
//			Point screenPos = ev.getPoint();
//			Rectangle screenRect = new Rectangle(screenPos.x - 2, screenPos.y - 2, 5, 5);
//			AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
//			Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
//			ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect,
//					mapPane.getMapContent().getCoordinateReferenceSystem());
//			Filter filternew = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
//			try {
//				FeatureCollection self = l.getFeatureSource().getFeatures(filternew);
//				FeatureIterator iter = self.features();
//				System.out.println("New : " + iter.hasNext());
//			} catch (IOException ex) {
//				Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
//			}
//
//		}
//
//		/*
//		 * Construct a 5x5 pixel rectangle centred on the mouse click position
//		 */
//		Point screenPos = ev.getPoint();
//		Rectangle screenRect = new Rectangle(screenPos.x - 2, screenPos.y - 2, 5, 5);
//
//		/*
//		 * Transform the screen rectangle into bounding box in the coordinate
//		 * reference system of our map context. Note: we are using a naive
//		 * method here but GeoTools also offers other, more accurate methods.
//		 */
//		AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
//		Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
//		ReferencedEnvelope bbox = new ReferencedEnvelope(worldRect,
//				mapPane.getMapContent().getCoordinateReferenceSystem());
//
//		/*
//		 * Create a Filter to select features that intersect with the bounding
//		 * box
//		 */
//		Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
//
//		/*
//		 * Use the filter to identify the selected features
//		 */
//		try {
//			SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);
//
//			SimpleFeatureIterator iter = selectedFeatures.features();
//			System.out.println("1 Has next ? " + iter.hasNext());
//
//			SimpleFeatureCollection selectedFeatures2 = featureSource2.getFeatures(filter);
//
//			SimpleFeatureIterator iter2 = selectedFeatures.features();
//			System.out.println("2 Has next ? " + iter2.hasNext());
//
//			Set<FeatureId> IDs = new HashSet<FeatureId>();
//			try {
//				while (iter.hasNext()) {
//					SimpleFeature feature = iter.next();
//					IDs.add(feature.getIdentifier());
//
//					System.out.println("   " + feature.getIdentifier());
//				}
//
//			} finally {
//				iter.close();
//			}
//
//			if (IDs.isEmpty()) {
//				System.out.println("   no feature selected");
//			}
//
//			displaySelectedFeatures(IDs);
//
//		} catch (Exception ex) {
//			System.out.println(ex.getMessage());
//			ex.printStackTrace();
//			return;
//		}
//	}
//}



//eatureSource<SimpleFeatureType, SimpleFeature> parcelSrc = ...  // 
//from parcels shapefile 
//FeatureSource<SimpleFeatureType, SimpleFeature> buildingSrc = ...  // 
//from buildings shapefile 
//
//FeatureCollection<SimpleFeatureType, SimpleFeature> buildingColl = 
//buildingSrc.getFeatures(); 
//
//WKTWriter wktWriter = new WKTWriter(); 
//FeatureIterator<SimpleFeature> iter = buildingColl.features(); 
//try { 
//    while (iter.hasNext()) { 
//        SimpleFeature building = iter.next(); 
//        Geometry geom = (Geometry) building.getDefaultGeometry(); 
//        String filterStr = "CONTAINS(the_geom, " + wktWriter.write(geom) + ")"; 
//        Filter filter = CQL.toFilter(filterStr); 
//
//        FeatureCollection<SimpleFeatureType, SimpleFeature> parcelColl 
//= parcelSrc.getFeatures(filter); 
//        if (!parcelColl.isEmpty()) { 
//                    FeatureIterator<SimpleFeature> parcelIter = 
//parcelColl.features(); 
//                    try { 
//                        while (parcelIter.hasNext()) { 
//                            // ... get whatever data for the parcel 
//that contains the building 
//                        } 
//
//                    } finally { 
//                        parcelIter.close(); 
//                    } 
//                } 
//            } 
//
//        } finally { 
//            iter.close(); 
//        } 
//
//    } 
