package com.cql.extension;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FunctionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class QueryFunction
  extends FunctionImpl
{
	 DataStore catalog;
  int maxResults;
  boolean single;
  
  public QueryFunction(Name name, DataStore catalog, List<Expression> args, Literal fallback, boolean single, int maxResults)
  {
    this.catalog = catalog;
    this.maxResults = maxResults;
    this.single = single;
    
    this.functionName = new FunctionNameImpl(name, args != null ? args.size() : -1);
    setName(name.getLocalPart());
    setFallbackValue(fallback);
    setParameters(args);
    if ((args.size() < 3) || (args.size() > 4)) {
      throw new IllegalArgumentException("QuerySingle function requires 3 or 4 arguments (feature type qualified name, cql filter, extracted attribute name and sort by clause");
    }
  }
  
  public Object evaluate(Object object)
  {
    FeatureIterator fi = null;
    try
    {
      String layerName = (String)((Expression)getParameters().get(0)).evaluate(object, String.class);
      if (layerName == null) {
        throw new IllegalArgumentException("The first argument should be a vector layer name");
      }
      FeatureType ft = this.catalog.getSchema(layerName);
      if (ft == null) {
        throw new IllegalArgumentException("Could not find vector layer " + layerName + " in the GeoServer catalog");
      }
      String attribute = (String)((Expression)getParameters().get(1)).evaluate(object, String.class);
      if (attribute == null) {
        throw new IllegalArgumentException("The second argument of the query function should be the attribute name");
      }
      CoordinateReferenceSystem crs = null;
      PropertyDescriptor ad = ft.getDescriptor(attribute);
      if (ad == null) {
        throw new IllegalArgumentException("Attribute " + attribute + " could not be found in layer " + layerName);
      }
      if ((ad instanceof GeometryDescriptor))
      {
        crs = ((GeometryDescriptor)ad).getCoordinateReferenceSystem();
        if (crs == null) {
          crs = ft.getCoordinateReferenceSystem();
        }
      }
      String cql = (String)((Expression)getParameters().get(2)).evaluate(object, String.class);
      if (cql == null) {
        throw new IllegalArgumentException("The third argument of the query function should be a valid (E)CQL filter");
      }
      Filter filter;
      try
      {
        filter = ECQL.toFilter(cql);
      }
      catch (Exception e)
      {
        throw new IllegalArgumentException("The third argument of the query function should be a valid (E)CQL filter", e);
      }
     
      Query query = new Query(null, filter, new String[] { attribute });
      
      query.setMaxFeatures(this.maxResults + 1);
      FeatureSource fs = catalog.getFeatureSource(layerName);
      fi = fs.getFeatures(query).features();
      List<Object> results = new ArrayList(this.maxResults);
      Feature f;
      while (fi.hasNext())
      {
        f = fi.next();
        Object value = f.getProperty(attribute).getValue();
        if (((value instanceof Geometry)) && (crs != null))
        {
          Geometry geom = (Geometry)value;
          geom.apply(new GeometryCRSFilter(crs));
        }
        results.add(value);
      }
      if (results.size() == 0) {
        return null;
      }
      if ((this.maxResults > 0) && (results.size() > this.maxResults) && (!this.single)) {
        throw new IllegalStateException("The query in " + getName() + " returns too many features, the limit is " + this.maxResults);
      }
      if (this.maxResults == 1) {
        return results.get(0);
      }
      return results;
    }
    catch (IOException e)
    {
      throw new RuntimeException("Failed to evaluated the query: " + e.getMessage(), e);
    }
    finally
    {
      if (fi != null) {
        fi.close();
      }
    }
  }
  
  static final class GeometryCRSFilter
    implements GeometryComponentFilter
  {
    CoordinateReferenceSystem crs;
    
    public GeometryCRSFilter(CoordinateReferenceSystem crs)
    {
      this.crs = crs;
    }
    
    public void filter(Geometry g)
    {
      g.setUserData(this.crs);
    }
  }
}
