<?xml version="1.0" encoding="UTF-8"?>
<gcs:Operation xmlns:gcs="urn:x-galdosinc:common:operations:galdos-common-services"
                           xmlns:urm="urn:x-galdosinc:indicio:user-role-manager"
                           xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"
                           xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">
    <csw:Transaction service="CSW" version="2.0.2">
        <csw:Insert>
            <rim:User id="urn:x-indicio:${loginName}:User">
                <rim:Name>
                    <rim:LocalizedString xml:lang="en"
                                           value="${firstName}"/>
                </rim:Name>
                <rim:PersonName firstName="${firstName}"
                    middleName="${middleName}"
                    lastName="${lastName}"/>
            </rim:User>
        </csw:Insert>
    </csw:Transaction>
    <urm:Transaction>
        <urm:Insert>
            <urm:UserContext userID="urn:x-indicio:${loginName}:User"
                loginName="${loginName}"
                password="${password}">
            </urm:UserContext>
        </urm:Insert>
    </urm:Transaction>
    <urm:Transaction>
        <urm:Update>
            <urm:UserContext loginName="${loginName}">
                <urm:RoleList>
                    <urm:UserRole name="${userRole}"/>
                </urm:RoleList>
            </urm:UserContext>
        </urm:Update>
    </urm:Transaction>
</gcs:Operation>
