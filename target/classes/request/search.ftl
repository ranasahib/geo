<GetRecords xmlns="http://www.opengis.net/cat/csw/2.0.2"
          xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
          xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0"
          xmlns:wrs="http://www.opengis.net/cat/wrs/1.0"
          xmlns:gml="http://www.opengis.net/gml/3.2"
          xmlns:ogc="http://www.opengis.net/ogc"
          xmlns:idx="urn:x-galdosinc:indicio:web-registry-service"
          service="CSW" version="2.0.2"
          startPosition="1" maxRecords="100"
          outputFormat="application/json" resultType="results">
          <Query typeNames="rim:RegistryObject">
            <ElementSetName typeNames="rim:RegistryObject">full</ElementSetName>
            <Constraint version="1.1.0">
              <ogc:Filter>
                   
          <ogc:PropertyIsLike wildCard="*" singleChar="#" escapeChar="!">
            <?indicio-case-sensitive false?>
            <ogc:PropertyName>rim:RegistryObject/Name/LocalizedString/@value</ogc:PropertyName>
            <ogc:Literal>*${searchTxt}*</ogc:Literal>
          </ogc:PropertyIsLike>
    
              </ogc:Filter>
            </Constraint>
          </Query>
        </GetRecords>