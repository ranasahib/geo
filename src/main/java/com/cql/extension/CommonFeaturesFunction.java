package com.cql.extension;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FunctionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.geometry.jts.GeometryCollector;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

public class CommonFeaturesFunction
  extends FunctionImpl
{
	
  long maxCoordinates;
  
  public CommonFeaturesFunction(Name name, List<Expression> args, Literal fallback, long maxCoordinates)
  {
    this.functionName = new FunctionNameImpl(name, args != null ? args.size() : -1);
    setName(name.getLocalPart());
    setFallbackValue(fallback);
    setParameters(args);
    this.maxCoordinates = maxCoordinates;
    if (args.size() < 2) {
      throw new IllegalArgumentException("CommonFeatures function requires a atleast 2 arguments, a ContentFeatureCollection");
    }
  }
  
  public Object evaluate(Object object)
	{
		List<SimpleFeature> commonFetaureList = null;
		SimpleFeatureCollection common = (SimpleFeatureCollection) ((Expression) getParameters().get(0))
				.evaluate(object, SimpleFeatureCollection.class);
		for (int i = 1; i < getParameters().size(); i++) {
			SimpleFeatureCollection features = (SimpleFeatureCollection) ((Expression) getParameters().get(i))
					.evaluate(object, SimpleFeatureCollection.class);
			FeatureIterator<SimpleFeature> iterator = features.features();
			commonFetaureList = new ArrayList<SimpleFeature>();
			try {
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					if (((ContentFeatureCollection) features).contains(feature)) {

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
}
