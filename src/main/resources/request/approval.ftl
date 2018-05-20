<gcs:Operation xmlns:gcs="urn:x-galdosinc:common:operations:galdos-common-services">
    <Transaction xmlns="urn:x-galdosinc:indicio:life-cycle-manager">
        <Update targetStatus="urn:oasis:names:tc:ebxml-regrep:StatusType:Approved">
            <csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" xmlns:ogc="http://www.opengis.net/ogc" xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0" xmlns:wrs="http://www.opengis.net/cat/wrs/1.0" service="CSW" version="2.0.2" resultType="results" maxRecords="100">
                <csw:Query typeNames="rim:${type}">
                    <csw:ElementSetName>brief</csw:ElementSetName>
                    <csw:Constraint version="1.1.0">
                        <ogc:Filter>
                            <wrs:RecordId>${id}</wrs:RecordId>
                        </ogc:Filter>
                    </csw:Constraint>
                </csw:Query>
            </csw:GetRecords>
        </Update>
    </Transaction>
</gcs:Operation>
