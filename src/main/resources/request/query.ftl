<?xml version="1.0" encoding="UTF-8"?>
<GetRecords xmlns="http://www.opengis.net/cat/csw/2.0.2" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:idx="urn:x-galdosinc:indicio:web-registry-service" xmlns:ogc="http://www.opengis.net/ogc" xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0" service="CSW" version="2.0.2" startPosition="1" maxRecords="10" resultType="results" outputFormat="application/json; charset=UTF-8">
   <Query typeNames="rim:RegistryObject">
      <?indicio-distinct-values true?>
      <?indicio-filter-joins rim:ExternalIdentifier=left-join?>
      <ElementSetName typeNames="rim:RegistryObject">full</ElementSetName>
      <Constraint version="1.1.0">
         <ogc:Filter>
			 <ogc:And>
				<ogc:PropertyIsEqualTo>
					<ogc:PropertyName>/$a/@objectType</ogc:PropertyName>
					<ogc:Literal>urn:ogc:def:ebRIM-ObjectType:OGC:Dataset</ogc:Literal>
				  </ogc:PropertyIsEqualTo>
                  ${queryXml}
           	 </ogc:And>
         </ogc:Filter>
      </Constraint>
   </Query>
</GetRecords>