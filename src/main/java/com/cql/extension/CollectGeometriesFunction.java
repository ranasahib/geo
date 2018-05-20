package com.cql.extension;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.Iterator;
import java.util.List;
import org.geotools.filter.FunctionImpl;
import org.geotools.filter.capability.FunctionNameImpl;
import org.geotools.geometry.jts.GeometryCollector;
import org.opengis.feature.type.Name;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;

public class CollectGeometriesFunction
  extends FunctionImpl
{
	
  long maxCoordinates;
  
  public CollectGeometriesFunction(Name name, List<Expression> args, Literal fallback, long maxCoordinates)
  {
    this.functionName = new FunctionNameImpl(name, args != null ? args.size() : -1);
    setName(name.getLocalPart());
    setFallbackValue(fallback);
    setParameters(args);
    this.maxCoordinates = maxCoordinates;
    if (args.size() != 1) {
      throw new IllegalArgumentException("CollectGeometries function requires a single argument, a collection of geometries");
    }
  }
  
  public Object evaluate(Object object)
  {
    List geometries = (List)((Expression)getParameters().get(0)).evaluate(object, List.class);
    if ((geometries == null) || (geometries.size() == 0)) {
      return new GeometryCollection(null, new GeometryFactory());
    }
    GeometryCollector collector = new GeometryCollector();
    collector.setFactory(null);
    collector.setMaxCoordinates(this.maxCoordinates);
    for (Iterator it = geometries.iterator(); it.hasNext();)
    {
      Geometry geometry = (Geometry)it.next();
      collector.add(geometry);
    }
    return collector.collect();
  }
}
