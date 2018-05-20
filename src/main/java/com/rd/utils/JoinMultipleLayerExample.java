package com.rd.utils;

import org.geotools.data.Join;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.FilterFactory2;

public class JoinMultipleLayerExample {
	public static void main(String[] args) {

		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

		Query query = new Query("SOI");

		Join join1 = new Join("Lake", ff.contains(ff.property("geom"), ff.property("l.geom")));
		join1.setAlias("l");
		query.getJoins().add(join1);

		//poi join
		Join join2 = new Join("POI", ff.disjoint(ff.property("geom"), ff.property("location")));
		join2.setFilter(ff.notEqual(ff.property("type"), ff.literal("tourism")));
		query.getJoins().add(join2);
		
		

	}
}
