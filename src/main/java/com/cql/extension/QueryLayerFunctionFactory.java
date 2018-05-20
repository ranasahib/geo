package com.cql.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.DataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.filter.FunctionFactory;
import org.geotools.util.logging.Logging;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public class QueryLayerFunctionFactory
  implements FunctionFactory
{
  static final Name COLLECT_GEOMETRIES = new NameImpl("collectGeometries");
  static final Name QUERY_COLLECTION = new NameImpl("queryCollection");
  static final Name QUERY_SINGLE = new NameImpl("querySingle");
  static final Name COMMON_FEATURES = new NameImpl("commonFeatures");
  static final Logger LOGGER = Logging.getLogger(QueryLayerFunctionFactory.class);
  List<FunctionName> functionNames;
  DataStore catalog;
  int maxFeatures = 1000;
  long maxCoordinates = 37449L;
  
  public QueryLayerFunctionFactory()
  {
    FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
    List<FunctionName> names = new ArrayList();
    names.add(ff.functionName(QUERY_SINGLE, -1));
    names.add(ff.functionName(QUERY_COLLECTION, -1));
    names.add(ff.functionName(COLLECT_GEOMETRIES, 1));
    names.add(ff.functionName(COMMON_FEATURES, -1));
    this.functionNames = Collections.unmodifiableList(names);
  }
  
  public long getMaxFeatures()
  {
    return this.maxFeatures;
  }
  
  public void setMaxFeatures(int maxFeatures)
  {
    if (maxFeatures <= 0) {
      throw new IllegalArgumentException("The max features retrieved by a query layer function must be a positive number");
    }
    this.maxFeatures = maxFeatures;
  }
  
  public void setMaxCoordinates(long maxCoordinates)
  {
    this.maxCoordinates = maxCoordinates;
  }
  
  public void setCatalog(DataStore catalog)
  {
    this.catalog = catalog;
  }
  
  public Function function(String name, List<Expression> args, Literal fallback)
  {
    return function(new NameImpl(name), args, fallback);
  }
  
  public Function function(Name name, List<Expression> args, Literal fallback)
  {
    if (!isInitialized()) {
      return null;
    }
    if (QUERY_SINGLE.equals(name)) {
      return new QueryFunction(QUERY_SINGLE, this.catalog, args, fallback, true, 1);
    }
    if (QUERY_COLLECTION.equals(name)) {
      return new QueryFunction(QUERY_COLLECTION, this.catalog, args, fallback, false, this.maxFeatures);
    }
    if (COLLECT_GEOMETRIES.equals(name)) {
      return new CollectGeometriesFunction(COLLECT_GEOMETRIES, args, fallback, this.maxCoordinates);
    }
    if (COMMON_FEATURES.equals(name)) {
        return new CommonFeaturesFunction(COMMON_FEATURES, args, fallback, this.maxCoordinates);
      }
    return null;
  }
  
  public List<FunctionName> getFunctionNames()
  {
    if (isInitialized()) {
      return this.functionNames;
    }
    return Collections.emptyList();
  }
  
  private boolean isInitialized()
  {
    if (this.catalog == null)
    {
      LOGGER.log(Level.INFO, "Looking for functions but the catalog still has not been set into QueryLayerFunctionFactory");
      
      return false;
    }
    return true;
  }
}
