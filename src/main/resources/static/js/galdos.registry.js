/**
 * Library of registry (INdicio) operations.
 *
 * @requires galdos.common
 * @requires jQuery <https://jquery.com/>
 */

 // TODO: Eventually change the default dataType to json (in registryDefaults and all methods) once all operations support JSON in INdicio

/** Registry default options. */
var registryDefaults = {
    lang: "en",                 // Default language for language querying and selection
    registryUrl: "http://103.62.95.146:8080/indicio",    // Default registry endpoint URL (absolute or relative)
    //dataType: "xml",          // Default AJAX dataType ("xml" or "json")
    // Global AJAX settings
    ajaxSettings: {
        /*beforeSend: function(jqXHR, settings) {
            setGlobalAuthentication(jqXHR);
        }*/
    }
};

/**
Operation parameters.  Each parameter takes the following properties:
    - name
        The name of the parameter.
    - type
        The type of parameter, "KVP" (default) or "DATA".
    - required
        Is the parameter is required (true) or not (false; default).
    - encode
        Should the value be URI encoded (true) or not (false; default).
    - value
        The value for the parameter (default value).
    - validate
        A validate method that receives the value to validate, returns true if
        valid, false otherwise.  Not required.
*/

// Common Parameters

// Substitute for any parameter
var anyParam = {
    name: "*",
    required: false
};

var requestParam = {
    name: "request",
    required: true
};

var serviceParam = {
    name: "service",
    required: true,
    value: "CSW-ebRIM",
    validate: function(val) {
        return $.inArray(val, ["CSW", "CSW-ebRIM", "WRS"]) != -1;
    }
};

var versionParam = {
    name: "version",
    required: true,
    value: "1.0.1",
    validate: function(val) {
        return $.inArray(val, ["2.0.2", "1.0.0", "1.0.1"]) != -1;
    }
};

var idParam = {
    name: "id",
    required: true
};

var viewParam = {
    name: "view"
};

var outputFormatParam = {
    name: "outputFormat",
    validate: function(val) {
        return (val.indexOf("application/xml") != -1)
            || (val.indexOf("application/json") != -1);
    }
};

var elementSetNameParam = {
    name: "elementSetName",
    validate: function(val) {
        return $.inArray(val, ["brief", "summary", "full"]) != -1;
    }
};

var includeParam = {
    name: "include"
};

var sskParam = {
    name: "ssk"
};

var resultTypeParam = {
    name: "resultType"
};

var startPositionParam = {
    name: "startPosition"
};

var maxRecordsParam = {
    name: "maxRecords"
};

var bodyParam = {
    name: "body",
    type: "DATA",
    required: true
};

var partsParam = {
    name: "parts",
    type: "ATTACHMENT"
};

// GetCapabilities Parameters

var sectionsParam = {
    name: "sections"
};

var acceptVersionsParam = {
    name: "acceptVersions"
};

var acceptFormatsParam = {
    name: "acceptFormats"
};

var updateSequenceParam = {
    name: "updateSequence"
};

// Stored Query (AdhocQuery) Parameters

var qidParam = {
    name: "qid",
    required: true
};

// GetDomain Parameters

var propertyNameParam = {
    name: "propertyName"
};

var parameterNameParam = {
    name: "parameterName"
};

var offsetParam = {
    name: "offset"
};

var limitParam = {
    name: "limit"
};

var sortParam = {
    name: "sort"
};

var includeNameParam = {
    name: "includeName"
};

// URM Parameters

var userIdParam = {
    name: "userId"
};

// Property Configuration Parameters

var keyParam = {
    name: "key",
    required: true
};

var valueParam = {
    name: "value",
    required: true,
    encode: true
};

var refreshParam = {
    name: "refresh"
};

var backupParam = {
    name: "backup"
};

// DB Configuration Parameters

var driverClassParam = {
    name: "driverClass",
    required: true
};

var jdbcUrlParam = {
    name: "jdbcUrl",
    required: true
};

var userParam = {
    name: "user",
    required: true
};

var passwordParam = {
    name: "password",
    required: true
};

// Admin User Configuration Parameters

var loginNameParam = {
    name: "loginName",
    required: true,
    encode: true
};

var passwordParam = {
    name: "password",
    required: true,
    encode: true
};

var firstNameParam = {
    name: "firstName",
    required: true,
    encode: true
};

var lastNameParam = {
    name: "lastName",
    required: true,
    encode: true
};

var email1Param = {
    name: "email1",
    encode: true
};

var email1TypeParam = {
    name: "email1Type",
    encode: true,
    value: "OfficeEmail"
};

// Diagnostics Parameters

var levelParam = {
    name: "level"
};

var filenameParam = {
    name: "filename"
};

var outputTypeParam = {
    name: "outputType",
    required: false,
    value: "text",
    validate: function(val) {
        return $.inArray(val, ["text", "zip"]) != -1;
    }
};

/* ========================================================================== */
/* Registry Objects */
/* ========================================================================== */

/** Gets the object with the given lang property value from a list of values
(i.e. names or descriptions).  Attempts to get the value that matches, or starts
with, the provided lang (default is provided), otherwise will return the first
value if useAny is true.  Returns an empty string if no matching lang was found. */
function getObjectByLang(internationalString, lang, useAny) {
    lang = typeof lang !== "undefined" ? lang : registryDefaults.lang;
    useAny = typeof useAny !== "undefined" ? useAny : true;
    // Return nothing if nothing was given
    if (typeof internationalString === "undefined") {
        return "";
    }
    var obj;
    // (A) JSON
    if (isJsonObject(internationalString)) {
        // 1) Get the first match
        obj = $.grep(internationalString, function(obj, i) {
            if (obj.lang === lang) {
                return true;
            }
        })[0];
        // 2) Get the first match (starting with)
        if (lang.indexOf("-") !== -1) {
            // Remove any region parts if present
            lang = lang.substring(0, lang.indexOf("-"));
        }
        if (typeof obj === "undefined") {
            obj = $.grep(internationalString, function(obj, i) {
                if (startsWith(obj.lang, lang)) {
                    return true;
                }
            })[0];
        }
        if (typeof obj === "undefined") {
            obj = $.grep(internationalString, function(obj, i) {
                if (startsWith(lang, obj.lang)) {
                    return true;
                }
            })[0];
        }
        // 3) Get the first non-empty object as a last resort
        if (useAny && (typeof obj === "undefined" || obj.value === "")) {
            $.each(internationalString, function() {
                if (this.value != "") {
                    obj = this;
                }
            });
        }
    }
    // (B) XML
    else {
        var localizedStrings = $(internationalString).find("rim\\:LocalizedString,LocalizedString");
        // 1) Get the first match
        $.each(localizedStrings, function() {
            if ($(this).attr("xml:lang") === lang) {
                obj = this;
                return false;
            }
        });
        // 2) Get the first match (starting with)
        if (lang.indexOf("-") !== -1) {
            // Remove any region parts if present
            lang = lang.substring(0, lang.indexOf("-"));
        }
        if (typeof obj === "undefined") {
            $.each(localizedStrings, function() {
                if (startsWith($(this).attr("xml:lang"), lang)) {
                    obj = this;
                    return false;
                }
            });
        }
        if (typeof obj === "undefined") {
            $.each(localizedStrings, function() {
                if (startsWith(lang, $(this).attr("xml:lang"))) {
                    obj = this;
                    return false;
                }
            });
        }
        // 3) Get the first non-empty object as a last resort
        if (useAny && (typeof obj === "undefined" || $(obj).attr("value") === "")) {
            $.each(localizedStrings, function() {
                if ($(this).attr("value") !== "") {
                    obj = this;
                    return false;
                }
            });
        }
    }
    // Return nothing if no matching localizedStrings were found
    return (typeof obj !== "undefined") ? obj : "";
}

/** In a list of objects (JSON or XML), return the object with the matching id. */
function getObjectById(objects, id, idName) {
    idName = typeof idName !== "undefined" ? idName : "id";
    // Return nothing if nothing was given
    if (typeof objects === "undefined") {
        return null;
    }
    var object = null;
    // (A) JSON
    if (isJsonObject(objects)) {
        $.each(objects, function() {
            if (this[idName] === id) {
                object = this;
                return false;
            }
        });
    }
    // (B) XML
    else {
        $.each($(objects).find("*").eq("0").children(), function() {
            if ($(this).attr(idName) === id) {
                object = this;
                return false;
            }
        });
    }
    return object;
}

/** Attaches a set of nodes (JSON) to a given select.  Adds nested nodes using
character (em-dash by default).  No sorting is performed. */
function attachSelectNodes(select, nodes, character, level) {
    character = character || "&mdash;";
    level = level || 1;
    if (nodes) {
        $.each(nodes, function(idx, node) {
            if (node.code) {
                var text = Array(level).join(character) + node.code + " (" + node.id + ")";
                $(select).append("<option value=\"" + node.id + "\" data-id=\"" + node.id + "\" "
                    + "data-code=\"" + node.code + "\" data-path=\"" + node.path + "\">" + text + "</option>");
                if (node.classificationNodes) {
                    attachSelectNodes(select, node.classificationNodes, character, level+1);
                }
            }
        });
    }
}

/* ========================================================================== */
/* Registry Request and Response Handling */
/* ========================================================================== */

/** Create a CSW-ebRIM transaction (JSON).  Supported types are insert (default),
full update, or delete by id.  Can specify a single entity or multiple entities.
Specify ids for the ids to delete. */
function createTransaction(parameters) {
    parameters.type = parameters.type || "insert";
    parameters.stringify = typeof parameters.stringify !== "undefined" ? parameters.stringify : true;
    var trx = {
        transaction: []
    };
    // Operation type
    var operation = [];
    if (parameters.type === "insert") {
        trx.transaction.push({insert: operation});
    } else if (parameters.type === "update") {
        trx.transaction.push({update: operation});
    } else if (parameters.type === "delete") {
        trx.transaction.push({delete: operation});
    }
    // Entities to operate on (insert or update)
    if (parameters.entities) {
        $.each(parameters.entities, function() {
            operation.push(this);
        });
    } else if (parameters.entity) {
        operation.push(parameters.entity);
    }
    // Ids to delete
    if (parameters.ids) {
        $.each(parameters.ids, function() {
            operation.push(this);
        });
    } else if (parameters.id) {
        operation.push(parameters.id);
    }
    if (parameters.stringify) {
        return JSON.stringify(trx);
    } else {
        return trx;
    }
}

/** Creates a GCS Transaction to Insert the given User and UserContext objects. */
function createNewUserTransaction(user, context) {
    var trx = {
        "operation": [
            {
                "transaction": [
                    {
                        "insert": [
                            user.toJSON()
                        ]
                    }
                ]
            },
            {
                "urm": [
                    {
                        "insert": [
                            context.toJSON()
                        ]
                    }
                ]
            }
        ]
    };
    return trx;
}

/** Creates a GCS Transaction to Update the given User and UserContext objects. */
function createUpdateUserTransaction(user, context) {
    var trx = {
        "operation": [
            {
                "transaction": [
                    {
                        "update": [
                            user.toJSON()
                        ]
                    }
                ]
            },
            {
                "urm": [
                    {
                        "update": [
                            context.toJSON()
                        ]
                    }
                ]
            }
        ]
    };
    return trx;
}

/** Gets the list of values from a GetDomain response (JSON). */
function getDomainValues(rsp) {
    var values = [];
    if (rsp && rsp.domainValues && rsp.domainValues.listOfValues) {
        $.each(rsp.domainValues.listOfValues, function() {
            // Ignores other attributes
            values.push(this.value);
        });
    }
    return values;
}

/** Gets the role names from a roleList (names and descriptions; JSON). */
function getRoleNames(roleList) {
    var names = [];
    $.each(roleList, function() {
        names.push(this.name);
    });
    return names;
}

/** Parses an XML URM response into an array of User Role objects (JSON). */
function parseUserRoles(response) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var roles = [];
    $.each($(response).find("urm\\:UserRole,UserRole"), function() {
        roles.push({
            name: $(this).attr("name"),
            description: $(this).attr("description")
        });
    });
    return roles;
}

/** Parses an XML URM response into an array of User Context objects (JSON). */
function parseUserContexts(response) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var contexts = [];
    $.each($(response).find("urm\\:UserContext,UserContext"), function() {
        var roles = [];
        $.each($(this).find("urm\\:UserRole,UserRole"), function() {
            roles.push($(this).attr("name"));
        });
        contexts.push({
            id: $(this).attr("userID"),
            loginName: $(this).attr("loginName"),
            roleList: roles
        });
    });
    return contexts;
}

/** Generates the XML from a given JSON roleList. */
function generateRoleListXml(roleList) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var roleListXml = "";
    $.each(roleList, function(idx) {
        roleListXml += '<urm:UserRole name="' + this + '" />';
        if (idx < roleList.length - 1) {
            roleListXml += '\r\n                    ';
        }
    });
    return roleListXml;
}

/** Generates the XML from a given JSON personName. */
function generatePersonNameXml(personName) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var personNameXml = "";
    if (personName) {
        personNameXml += '<rim:PersonName';
        if (personName.firstName) {
            personNameXml += ' firstName="' + personName.firstName + '"';
        }
        if (personName.middleName) {
            personNameXml += ' middleName="' + personName.middleName + '"';
        }
        if (personName.lastName) {
            personNameXml += ' lastName="' + personName.lastName + '"';
        }
        personNameXml += ' />';
    }
    return personNameXml;
}

/** Generates the XML from a given JSON addresses. */
function generateAddressesXml(addresses) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var addressesXml = "";
    if (addresses) {
        $.each(addresses, function(idx) {
            addressesXml += '<rim:Address';
            if (this.streetNumber) {
                addressesXml += ' streetNumber="' + this.streetNumber + '"';
            }
            if (this.street) {
                addressesXml += ' street="' + this.street + '"';
            }
            if (this.city) {
                addressesXml += ' city="' + this.city + '"';
            }
            if (this.stateOrProvince) {
                addressesXml += ' stateOrProvince="' + this.stateOrProvince + '"';
            }
            if (this.country) {
                addressesXml += ' country="' + this.country + '"';
            }
            if (this.postalCode) {
                addressesXml += ' postalCode="' + this.postalCode + '"';
            }
            addressesXml += ' />';
            if (idx < addresses.length - 1) {
                addressesXml += '\r\n                ';
            }
        });
    }
    return addressesXml;
}

/** Generates the XML from a given JSON telephoneNumbers. */
function generateTelephoneNumbersXml(telephoneNumbers) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var telephoneNumbersXml = "";
    if (telephoneNumbers) {
        $.each(telephoneNumbers, function(idx) {
            telephoneNumbersXml += '<rim:TelephoneNumber';
            if (this.countryCode) {
                telephoneNumbersXml += ' countryCode="' + this.countryCode + '"';
            }
            if (this.areaCode) {
                telephoneNumbersXml += ' areaCode="' + this.areaCode + '"';
            }
            if (this.number) {
                telephoneNumbersXml += ' number="' + this.number + '"';
            }
            if (this.extension) {
                telephoneNumbersXml += ' extension="' + this.extension + '"';
            }
            // Default phoneType is OfficePhone
            if (this.phoneType) {
                telephoneNumbersXml += ' phoneType="' + this.phoneType + '"';
            } else {
                telephoneNumbersXml += ' phoneType="OfficePhone"';
            }
            telephoneNumbersXml += ' />';
            if (idx < telephoneNumbers.length - 1) {
                telephoneNumbersXml += '\r\n                ';
            }
        });
    }
    return telephoneNumbersXml;
}

/** Generates the XML from a given JSON emailAddresses. */
function generateEmailAddressesXml(emailAddresses) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    var emailAddressesXml = "";
    if (emailAddresses) {
        $.each(emailAddresses, function(idx) {
            emailAddressesXml += '<rim:EmailAddress';
            if (this.address) {
                emailAddressesXml += ' address="' + this.address + '"';
            }
            // Default type is OfficeEmail
            if (this.type) {
                emailAddressesXml += ' type="' + this.type + '"';
            } else {
                emailAddressesXml += ' type="OfficeEmail"';
            }
            emailAddressesXml += ' />';
            if (idx < emailAddresses.length - 1) {
                emailAddressesXml += '\r\n                ';
            }
        });
    }
    return emailAddressesXml;
}

/** Creates a new user and user context (via a GCS Transaction) with the given
user properties.  Requires a loginName and password, in addition to the optional
user properties (personName, addresses, telephoneNumbers, emailAddresses).  If
no roleList is given, it is assumed to be a RegistryUser. */
function createUser(properties, settings, registryUrl) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    if (!properties.loginName || !properties.password) {
        var msg = "Cannot create a user without a loginName and password.";
        if (DEBUG) {
            console.log(msg);
        }
        return (new $.Deferred()).reject(undefined, "error", msg);
    }
    // The User Context userID must be a UUID
    if (!properties.id || !startsWith(properties.id, "urn:")) {
        properties.id = generateUuidUrn();
    }
    // Ensure a minimum role of RegistryUser
    if (!properties.roleList) {
        properties.roleList = ["RegistryUser"];
    }
    var trx = '<gcs:Operation xmlns:gcs="urn:x-galdosinc:common:operations:galdos-common-services"\r\n'
        + '    xmlns:urm="urn:x-galdosinc:indicio:user-role-manager"\r\n'
        + '    xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"\r\n'
        + '    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">\r\n'
        + '    <!-- (1) Add a user object -->\r\n'
        + '    <csw:Transaction service="CSW" version="2.0.2">\r\n'
        + '        <csw:Insert>\r\n'
        + '            <rim:User id="${id}">\r\n'
        + '                ${addresses}\r\n'
        + '                ${personName}\r\n'
        + '                ${telephoneNumbers}\r\n'
        + '                ${emailAddresses}\r\n'
        + '            </rim:User>\r\n'
        + '        </csw:Insert>\r\n'
        + '    </csw:Transaction>\r\n'
        + '    <!-- (2) Add a user context (login) to the user -->\r\n'
        + '    <urm:Transaction>\r\n'
        + '        <urm:Insert>\r\n'
        + '            <urm:UserContext userID="${id}" loginName="${loginName}" password="${password}" />\r\n'
        + '        </urm:Insert>\r\n'
        + '    </urm:Transaction>\r\n'
        + '    <!-- (3) Set the user role(s) -->\r\n'
        + '    <urm:Transaction>\r\n'
        + '        <urm:Update>\r\n'
        + '            <urm:UserContext loginName="${loginName}">\r\n'
        + '                <urm:RoleList>\r\n'
        + '                    ${roleList}\r\n'
        + '                </urm:RoleList>\r\n'
        + '            </urm:UserContext>\r\n'
        + '        </urm:Update>\r\n'
        + '    </urm:Transaction>\r\n'
        + '</gcs:Operation>';
    // Parse properties
    var roleList = generateRoleListXml(properties.roleList);
    var addresses = generateAddressesXml(properties.addresses);
    var personName = generatePersonNameXml(properties.personName);
    var telephoneNumbers = generateTelephoneNumbersXml(properties.telephoneNumbers);
    var emailAddresses = generateEmailAddressesXml(properties.emailAddresses);
    // Replace properties
    trx = replaceParams(trx, {
        loginName: properties.loginName,
        password: properties.password,
        roleList: roleList,
        id: properties.id,
        addresses: addresses,
        personName: personName,
        telephoneNumbers: telephoneNumbers,
        emailAddresses: emailAddresses
    });
    // Submit the GCS transaction
    return gcsTransaction({
        body: trx
    }, settings, registryUrl);
}

/** Updates an existing user and user context (via a GCS Transaction) with the
given user properties.  Requires the id to be provided, although it cannot be
updated.  Any properties not supported by this method are discarded
(e.g. Slots, etc). */
function updateUser(properties, settings, registryUrl) {
    // TODO: Remove this function and refactor dependent files to use JSON directly
    if (!properties.id) {
        var msg = "Cannot update a user without an id.";
        if (DEBUG) {
            console.log(msg);
        }
        return (new $.Deferred()).reject(undefined, "error", msg);
    }
    var trx = '<gcs:Operation xmlns:gcs="urn:x-galdosinc:common:operations:galdos-common-services"\r\n'
        + '    xmlns:urm="urn:x-galdosinc:indicio:user-role-manager"\r\n'
        + '    xmlns:csw="http://www.opengis.net/cat/csw/2.0.2"\r\n'
        + '    xmlns:rim="urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0">\r\n'
        + '    <!-- (1) Update user object -->\r\n'
        + '    <csw:Transaction service="CSW" version="2.0.2">\r\n'
        + '        <csw:Update>\r\n'
        + '            <rim:User id="${id}">\r\n'
        + '                ${addresses}\r\n'
        + '                ${personName}\r\n'
        + '                ${telephoneNumbers}\r\n'
        + '                ${emailAddresses}\r\n'
        + '            </rim:User>\r\n'
        + '        </csw:Update>\r\n'
        + '    </csw:Transaction>\r\n'
        + '    <!-- (2) Update user context -->\r\n'
        + '    <urm:Transaction>\r\n'
        + '        <urm:Update>\r\n'
        + '            <urm:UserContext userID="${id}"';
    if (properties.loginName) {
        trx += ' loginName="${loginName}"';
    }
    if (properties.password) {
        trx += ' password="${password}"';
    }
    trx += '>\r\n'
        + '                <urm:RoleList>\r\n'
        + '                    ${roleList}\r\n'
        + '                </urm:RoleList>\r\n'
        + '            </urm:UserContext>\r\n'
        + '        </urm:Update>\r\n'
        + '    </urm:Transaction>\r\n'
        + '</gcs:Operation>';
    // Parse properties
    var roleList = generateRoleListXml(properties.roleList);
    var addresses = generateAddressesXml(properties.addresses);
    var personName = generatePersonNameXml(properties.personName);
    var telephoneNumbers = generateTelephoneNumbersXml(properties.telephoneNumbers);
    var emailAddresses = generateEmailAddressesXml(properties.emailAddresses);
    // Replace properties
    trx = replaceParams(trx, {
        loginName: properties.loginName,
        password: properties.password,
        roleList: roleList,
        id: properties.id,
        addresses: addresses,
        personName: personName,
        telephoneNumbers: telephoneNumbers,
        emailAddresses: emailAddresses
    });
    // Submit the GCS transaction
    return gcsTransaction({
        body: trx
    }, settings, registryUrl);
}

/** Gets the data type based on the outputFormat parameter value; xml (default)
or json. */
function getDataType(parameters, parameterDataType) {
    var dataType = undefined;
    // (A) From parameter value (outputFormat)
    if (typeof parameters !== "undefined") {
        // Assumes parameters is an array, otherwise a string
        if (typeof parameters === "object") {
            $.each(parameters, function(idx, param) {
                if (param.name == "outputFormat" && typeof param.value !== "undefined") {
                    dataType = param.value.indexOf("json") != -1 ? "json" : "xml";
                    return false;
                }
            });
        } else if (typeof parameters === "string") {
            dataType = parameters;
        }
    }
    // (B) From parameter value (outputFormat)
    if (typeof dataType === "undefined" && parameterDataType) {
        dataType = parameterDataType;
    }
    // (C) From global default, otherwise XML
    if (typeof dataType === "undefined") {
        dataType = registryDefaults.dataType || "xml";
    }
    return dataType;
}

/** Gets the data type based on the request string; xml (default) or json. */
function getDataTypeFromRequest(request, parameterDataType) {
    var dataType = undefined;
    // (A) From request body (outputFormat attribute)
    if ($.isXMLDoc(request)) {
        var requestStr = $.parseXML(request);
        var outputFormat = requestStr.match(/outputFormat=['"](.*?)['"]/g);
        if (outputFormat != null && outputFormat.length > 0) {
            dataType = outputFormat[0].indexOf("json") != -1 ? "json" : "xml";
        }
    } else {
        // Default to JSON for JSON requests
        if (isJson(request)) {
            dataType = "json";
        }
        var requestStr = request;
        if (isJsonObject(request)) {
            requestStr = JSON.stringify(requestStr);
        }
        // XML string or JSON string
        var outputFormat = requestStr.match(/outputFormat=['"](.*?)['"]/g);
        if (outputFormat != null && outputFormat.length > 0) {
            dataType = outputFormat[0].indexOf("json") != -1 ? "json" : "xml";
        }
        // TODO: Also extend this case to XML
        // Don't look at nested outputFormat values
        var isNested = requestStr.match(/['"]lcm['"]:.*['"]getrecords['"]:/g);
        requestStr = isNested && isNested.length > 0 ? requestStr.replace(/(.*['"]getrecords['"].*)(outputFormat)/g, "$1IGNORE=") : requestStr;
        var outputFormat = requestStr.match(/['"]?outputFormat['"]?:\s*['"](.*?)['"]/g);
        if (outputFormat != null && outputFormat.length > 0) {
            if (requestStr)
            dataType = outputFormat[0].indexOf("json") != -1 ? "json" : "xml";
        }
    }
    // (B) From parameter value (outputFormat)
    if (typeof dataType === "undefined" && parameterDataType) {
        dataType = parameterDataType;
    }
    // (C) From global default, otherwise XML
    if (typeof dataType === "undefined") {
        dataType = registryDefaults.dataType || "xml";
    }
    return dataType;
}

/** Gets the content type based on the request string; application/xml (default)
or application/json. */
function getContentTypeFromRequest(request) {
    var contentType = "application/xml;charset=UTF-8";
    if (isJson(request)) {
        contentType = "application/json;charset=UTF-8";
    }
    // Assumes XML (object or string) otherwise
    return contentType;
}

/** Takes an array of registry responses (JSON) and consolidates them into a
single response object. */
function consolidateResponses(responses) {
    var searchResults = [];
    var result = {
        numberOfRecordsMatched: undefined,
        numberOfRecordsReturned: undefined,
        nextRecord: undefined,
        elementSet: undefined,
        timestamp: undefined,
        searchResults: searchResults
    };
    var allResults = false;
    $.each(responses, function(idx, rsp) {
        // Add the search results
        if (typeof rsp !== "undefined" && typeof rsp.searchResults !== "undefined") {
            $.each(rsp.searchResults, function() {
                searchResults.push(this);
            });
        }
        // Update other query metadata
        if (typeof rsp.elementSet !== "undefined") {
            result.elementSet = rsp.elementSet;
        }
        if (typeof rsp.numberOfRecordsMatched !== "undefined") {
            result.numberOfRecordsMatched = rsp.numberOfRecordsMatched;
        }
        if (typeof rsp.nextRecord !== "undefined") {
            if (rsp.nextRecord == "0") {
                result.nextRecord = rsp.nextRecord;
                allResults = true;
            } else if (!allResults && rsp.nextRecord > result.nextRecord) {
                result.nextRecord = rsp.nextRecord;
            }
        }
        if (typeof rsp.timestamp !== "undefined") {
            if (result.timestamp == null || rsp.timestamp > result.timestamp) {
                result.timestamp = rsp.timestamp;
            }
        }
    });
    // Adjust the number of records reported
    result.numberOfRecordsReturned = searchResults.length;
    if (result.numberOfRecordsMatched < result.numberOfRecordsReturned) {
        result.numberOfRecordsMatched = result.numberOfRecordsReturned;
    }
    return result;
}

/** Gets the nextRecord value from the query response. */
function getNextRecord(data) {
    // Assumes XML, otherwise JSON
    var nextRecord;
    if ($.isXMLDoc(data)){
        nextRecord = $(data).find("csw\\:SearchResults,SearchResults").attr("nextRecord");
    } else if (typeof data.nextRecord !== "undefined") {
        nextRecord = data.nextRecord;
    }
    // Cast as a number
    if (typeof nextRecord !== "undefined" && typeof nextRecord !== "number") {
        nextRecord = parseInt(nextRecord);
    }
    return nextRecord;
}

/** Gets the numberOfRecordsReturned value from the query response. */
function getNumberOfRecordsReturned(data) {
    // Assumes XML, otherwise JSON
    var numberOfRecordsReturned;
    if ($.isXMLDoc(data)){
        numberOfRecordsReturned = $(data).find("csw\\:SearchResults,SearchResults").attr("numberOfRecordsReturned");
    } else if (typeof data.numberOfRecordsReturned !== "undefined") {
        numberOfRecordsReturned = data.numberOfRecordsReturned;
    }
    // Cast as a number
    if (typeof numberOfRecordsReturned !== "undefined" && typeof numberOfRecordsReturned !== "number") {
        numberOfRecordsReturned = parseInt(numberOfRecordsReturned);
    }
    return numberOfRecordsReturned;
}

/** Gets the numberOfRecordsMatched value from the query response. */
function getNumberOfRecordsMatched(data) {
    // Assumes XML, otherwise JSON
    var numberOfRecordsMatched;
    if ($.isXMLDoc(data)){
        numberOfRecordsMatched = $(data).find("csw\\:SearchResults,SearchResults").attr("numberOfRecordsMatched");
    } else if (typeof data.numberOfRecordsMatched !== "undefined") {
        numberOfRecordsMatched = data.numberOfRecordsMatched;
    }
    // Cast as a number
    if (typeof numberOfRecordsMatched !== "undefined" && typeof numberOfRecordsMatched !== "number") {
        numberOfRecordsMatched = parseInt(numberOfRecordsMatched);
    }
    return numberOfRecordsMatched;
}

/** Gets the total number of records inserted. */
function getTotalInserted(data) {
    // Assumes XML, otherwise JSON
    var totalInserted;
    if ($.isXMLDoc(data)){
        totalInserted = $(data).find("csw\\:totalInserted,totalInserted").text();
    } else if (data.transactionSummary && typeof data.transactionSummary.totalInserted !== "undefined") {
        totalInserted = data.transactionSummary.totalInserted;
    }
    // Cast as a number
    if (typeof totalInserted !== "undefined" && typeof totalInserted !== "number") {
        totalInserted = parseInt(totalInserted);
    }
    return totalInserted;
}

/** Gets the total number of records updated. */
function getTotalUpdated(data) {
    // Assumes XML, otherwise JSON
    var totalUpdated;
    if ($.isXMLDoc(data)){
        totalUpdated = $(data).find("csw\\:totalUpdated,totalUpdated").text();
    } else if (data.transactionSummary && typeof data.transactionSummary.totalUpdated !== "undefined") {
        totalUpdated = data.transactionSummary.totalUpdated;
    }
    // Cast as a number
    if (typeof totalUpdated !== "undefined" && typeof totalUpdated !== "number") {
        totalUpdated = parseInt(totalUpdated);
    }
    return totalUpdated;
}

/** Gets the total number of records deleted. */
function getTotalDeleted(data) {
    // Assumes XML, otherwise JSON
    var totalDeleted;
    if ($.isXMLDoc(data)){
        totalDeleted = $(data).find("csw\\:totalDeleted,totalDeleted").text();
    } else if (data.transactionSummary && typeof data.transactionSummary.totalDeleted !== "undefined") {
        totalDeleted = data.transactionSummary.totalDeleted;
    }
    // Cast as a number
    if (typeof totalDeleted !== "undefined" && typeof totalDeleted !== "number") {
        totalDeleted = parseInt(totalDeleted);
    }
    return totalDeleted;
}

/** Determines if the array of parameters contains one with the given name
(returns the index of the value in the array). */
function getParameterIndex(parameters, name) {
    var index = undefined;
    $.each(parameters, function(idx, param) {
        if (param.name && param.name == name) {
            index = idx;
            return false;
        }
    });
    return index;
}

/** Generic registry operation.  Requires operation-specific configuration.
Options are the operation specific options, settings are any AJAX settings to
override, operationParameters are the defined operation parameters, parameters
are the user-supplied parameters. */
function operation(name, options, settings, operationParameters, parameters, registryUrl) {
    registryUrl = registryUrl || registryDefaults.registryUrl;
    var opts = $.extend({
        method: "GET",
        dataType: "xml",
        contentType: "application/x-www-form-urlencoded;charset=UTF-8"
    }, options);
    var url = registryUrl + opts.baseUrl + "?";
    var data = undefined;
    var parts = undefined;
    var params = $.extend([], operationParameters);
    // Allow for any parameter (e.g. stored queries)
    var allowAny = typeof getParameterIndex(params, anyParam.name) !== "undefined" ? true : false;
    // Combine operation and passed parameters
    if (parameters) {
        var unsupportedParam = undefined;
        $.each(parameters, function(name, value) {
            var index = getParameterIndex(params, name);
            if (!allowAny && (!name || typeof index === "undefined")) {
                unsupportedParam = name;
                return false;
            } else {
                if (typeof index === "undefined") {
                    params.push({
                        name: name,
                        value: value
                    });
                } else {
                    params[index].value = value;
                }
            }
        });
        // Fail if the parameter is unsupported
        if (unsupportedParam) {
            var msg = "Unsupported parameter " + unsupportedParam;
            if (DEBUG) {
                console.log(msg);
            }
            return (new $.Deferred()).reject(undefined, "error", msg);
        }
    }
    var missingParam = undefined;
    var invalidParam = undefined;
    // Parse and validate parameters
    $.each(params, function(idx, param) {
        if (param.required && typeof param.value === "undefined") {
            missingParam = param.name;
            return false;
        } else if ((typeof param.type === "undefined" || param.type == "KVP") && typeof param.value !== "undefined") {
            url += "&" + param.name + "=" + (param.encode ? encodeURIComponent(param.value) : param.value);
        } else if (param.type == "DATA") {
            // Assumes only 1 data parameter
            data = param.value;
        } else if (param.type == "ATTACHMENT") {
            // Assumes only 1 attachments parameter (holds 1 or more)
            parts = param.value;
        }
        // Validate, if a function was given
        if (typeof param.validate === "function" && param.value) {
            var valid = param.validate.call(this, param.value);
            if (!valid) {
                invalidParam = param;
                return false;
            }
        }
    });
    // Fail if any required parameters are missing
    if (missingParam) {
        var msg = "Missing required parameter " + missingParam;
        if (DEBUG) {
            console.log(msg);
        }
        return (new $.Deferred()).reject(undefined, "error", msg);
    }
    // Fail if a parameter failed validation
    if (invalidParam) {
        var msg = "Invalid parameter value for " + invalidParam.name + " (" + invalidParam.value + ")";
        if (DEBUG) {
            console.log(msg);
        }
        return (new $.Deferred()).reject(undefined, "error", msg);
    }
    if (DEBUG) {
        console.log("Executing " + name + " operation");
        console.log(url);
        if (typeof data !== "undefined") {
            console.log("POST data: " + data);
        }
        if (typeof parts !== "undefined") {
            console.log("Attachments:");
            $.each(parts, function(idx, part) {
                console.log("  " + part.id + " (" + part.mimetype + ")");
            });
        }
    }
    var contentType = getContentTypeFromRequest(data);
    data = isJsonObject(data) ? JSON.stringify(data) : data;
    if (typeof parts !== "undefined") {
        return multipartRelated(url, data, contentType, parts);
    } else {
        // Passed settings override dataType (outputFormat)
        var dataType = settings && settings.dataType ? settings.dataType : getDataType(params, opts.dataType);
        // Initial AJAX settings
        var ajaxSettings = {
            method: opts.method,
            url: url,
            contentType: opts.contentType,
            dataType: dataType
        };
        // Modify settings for POST
        if (typeof data !== "undefined") {
            ajaxSettings.data = data;
            ajaxSettings.processData = false;
            var dataType = settings && settings.dataType ? settings.dataType : getDataTypeFromRequest(data, dataType);
            ajaxSettings.dataType = dataType;
            ajaxSettings.contentType = contentType;
        }
        // Override settings based on global defaults and passed values
        var ajaxSettings = $.extend(ajaxSettings, registryDefaults.ajaxSettings, settings);
        return $.ajax(ajaxSettings);
    }
}

/** Pages a given query with the given parameters (pages over startPosition and
maxRecords).  Can pass in callback functions for each page iteration, or just
rely on the returned promise for the final array of responses. */
function pageQuery(query, parameters, settings, successCallback, errorCallback, dfd, data) {
    dfd = dfd || new $.Deferred();
    data = data || [];
    $.when(query.call(this, parameters, settings)).done(function(d, textStatus, jqXHR) {
        data.push(d);
        var nextRecord = getNextRecord(d);
        var maxRecords = getNumberOfRecordsReturned(d);
        if (typeof successCallback === "function") {
            successCallback(d, textStatus, jqXHR);
        }
        if (nextRecord > 0) {
            parameters.startPosition = nextRecord;
            parameters.maxRecords = maxRecords;
            pageQuery(query, parameters, settings, successCallback, errorCallback, dfd, data);
        } else {
            dfd.resolve(data);
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        if (typeof errorCallback === "function") {
            errorCallback(jqXHR, textStatus, errorThrown);
        }
        dfd.reject(jqXHR, textStatus, errorThrown);
    });
    return dfd.promise();
}

/** Pages a query and returns the consolidated response (JSON only). */
function pageQueryConsolidated(query, parameters, settings, successCallback, errorCallback) {
    var dfd = new $.Deferred();
    $.when(pageQuery(query, parameters, settings, successCallback, errorCallback)).done(function(data) {
        var d = consolidateResponses(data);
        dfd.resolve(d);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        dfd.reject(jqXHR, textStatus, errorThrown);
    });
    return dfd.promise();
}

/** Gets the exception text messages from an ExceptionReport (XML). */
function getExceptionMessages(rsp) {
    var messages = [];
    if ($.isXMLDoc(rsp)) {
        $.each($(rsp).find("ows\\:ExceptionText,ExceptionText"), function() {
            messages.push($(this).text());
        });
    } else if (isJson(rsp)) {
        var rspObj = rsp;
        if (isJsonString(rsp)) {
            rspObj = JSON.parse(rsp);
        }
        $.each(rspObj.exception, function(idx, exception) {
            $.each(exception.text, function(i, text) {
                messages.push(text);
            });
        });
    } else {
        var rspDoc = $.parseXML(rsp);
        $.each($(rspDoc).find("ows\\:ExceptionText,ExceptionText"), function() {
            messages.push($(this).text());
        });
    }
    return messages;
}

/** Substitute GET parameters into POST body (i.e. startPosition and maxRecords,
to allow for easier paging, outputFormat, and resultType).  Substitutes the
first value only (not global). */
function updatePostParameters(parameters) {
    if (parameters.body) {
        if (parameters.startPosition) {
            if (typeof parameters.body === "string") {
                parameters.body = parameters.body.replace(/(startPosition=['"]).*?(['"])/, "$1" + parameters.startPosition + "$2");
            } else if (parameters.body.getrecords) {
                parameters.body.getrecords.startPosition = parameters.startPosition;
            }
            delete parameters.startPosition;
        }
        if (parameters.maxRecords) {
            if (typeof parameters.body === "string") {
                parameters.body = parameters.body.replace(/(maxRecords=['"]).*?(['"])/, "\$1" + parameters.maxRecords + "$2");
            } else if (parameters.body.getrecords) {
                parameters.body.getrecords.maxRecords = parameters.maxRecords;
            }
            delete parameters.maxRecords;
        }
        if (parameters.outputFormat) {
            if (typeof parameters.body === "string") {
                parameters.body = parameters.body.replace(/(outputFormat=['"]).*?(['"])/, "\$1" + parameters.outputFormat + "$2");
            } else if (parameters.body.getrecords) {
                parameters.body.getrecords.outputFormat = parameters.outputFormat;
            }
            delete parameters.outputFormat;
        }
        if (parameters.resultType) {
            if (typeof parameters.body === "string") {
                parameters.body = parameters.body.replace(/(resultType=['"]).*?(['"])/, "\$1" + parameters.resultType + "$2");
            } else if (parameters.body.getrecords) {
                parameters.body.getrecords.resultType = parameters.resultType;
            }
            delete parameters.resultType;
        }
    }
}

/* ========================================================================== */
/* Discovery Operations */
/* ========================================================================== */

/** Returns the registry capabilities document. */
function getCapabilities(parameters, settings, registryUrl) {
    var operationName = "GetCapabilities";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, requestParam, {
            value: operationName
        }),
        serviceParam,
        versionParam,
        sectionsParam,
        acceptVersionsParam,
        acceptFormatsParam,
        updateSequenceParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/query"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a given record by id. */
function getRecordById(parameters, settings, registryUrl) {
    var operationName = "GetRecordById";
    if (parameters && parameters.body) {
        // POST
        var operationParameters = $.extend(true, [], [
            bodyParam,
            includeParam,
            sskParam,
            viewParam,
            outputFormatParam
        ]);
        // Substitute GET parameters into POST body
        updatePostParameters(parameters);
        return new operation(operationName, {
            method: "POST",
            baseUrl: "/query",
            dataType: getDataTypeFromRequest(parameters.body),
            contentType: getContentTypeFromRequest(parameters.body)
        }, settings, operationParameters, parameters, registryUrl);
    } else {
        // GET
        var operationParameters = $.extend(true, [], [
            $.extend(true, {}, requestParam, {
                value: operationName
            }),
            serviceParam,
            versionParam,
            idParam,
            outputFormatParam,
            elementSetNameParam,
            resultTypeParam,
            includeParam,
            sskParam,
            startPositionParam,
            maxRecordsParam,
            viewParam
        ]);
        return new operation(operationName, {
            method: "GET",
            baseUrl: "/query"
        }, settings, operationParameters, parameters, registryUrl);
    }
}

/** Gets records by OGC Filter. */
function getRecords(parameters, settings, registryUrl) {
    var operationName = "GetRecords";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        includeParam,
        sskParam,
        viewParam,
        outputFormatParam
    ]);
    // Substitute GET parameters into POST body
    updatePostParameters(parameters);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/query",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a given repository item by id. */
function getRepositoryItem(parameters, settings, registryUrl) {
    var operationName = "GetRepositoryItem";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, requestParam, {
            value: operationName
        }),
        serviceParam,
        versionParam,
        idParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/query"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets the repository item by id as binary (base64). */
function getRepositoryItemBinary(id, username, password, registryUrl) {
    registryUrl = registryUrl || registryDefaults.registryUrl;
    var method = "GET";
    var url = registryUrl + "/query?"
        + "request=GetRepositoryItem"
        + "&service=CSW-ebRIM"
        + "&version=1.0.1"
        + "&id=" + id;
    var dfd = new $.Deferred();
    // Must construct a custom XHR to get binary content (jQuery is restricted to a few dataTypes)
    var xhr = new XMLHttpRequest();
    xhr.open(method, url, true);
    // Get as array buffer to easily encode as base64
    xhr.responseType = "arraybuffer";
    if (username && password) {
        setAuthentication(xhr, username, password);
    }
    xhr.onload = function() {
        if (xhr.status == 200) {
            var data = arrayBufferToBase64(xhr.response);
            dfd.resolve(data, xhr.statusText, xhr);
        } else {
            dfd.reject(xhr, "Error", xhr.statusText);
        }
    };
    xhr.onerror = function() {
        dfd.reject(xhr, "Network Error", xhr.statusText);
    };
    xhr.send();
    return dfd.promise();
}

/** Query against the registry using a stored query (adhoc query). */
function storedQuery(parameters, settings, registryUrl) {
    var operationName = "Query";
    if (parameters && parameters.body) {
        // POST
        var operationParameters = $.extend(true, [], [
            bodyParam,
            $.extend(true, {}, resultTypeParam, {
                // Default of "hits" is not useful
                value: "results"
            }),
            outputFormatParam,
            elementSetNameParam,
            resultTypeParam,
            includeParam,
            sskParam,
            startPositionParam,
            maxRecordsParam,
            viewParam
        ]);
        // Substitute GET parameters into POST body
        updatePostParameters(parameters);
        return new operation(operationName, {
            method: "POST",
            baseUrl: "/query",
            dataType: getDataTypeFromRequest(parameters.body),
            contentType: getContentTypeFromRequest(parameters.body)
        }, settings, operationParameters, parameters, registryUrl);
    } else {
        // GET
        var operationParameters = $.extend(true, [], [
            $.extend(true, {}, requestParam, {
                value: operationName
            }),
            serviceParam,
            versionParam,
            qidParam,
            $.extend(true, {}, resultTypeParam, {
                // Default of "hits" is not useful
                value: "results"
            }),
            outputFormatParam,
            elementSetNameParam,
            resultTypeParam,
            includeParam,
            sskParam,
            startPositionParam,
            maxRecordsParam,
            viewParam,
            anyParam
        ]);
        return new operation(operationName, {
            method: "GET",
            baseUrl: "/query"
        }, settings, operationParameters, parameters, registryUrl);
    }
}

/** Gets the domain values for the given propertyName or parameterName. */
function getDomain(parameters, settings, registryUrl) {
    var operationName = "GetDomain";
    if (parameters && parameters.body) {
        // POST
        var operationParameters = $.extend(true, [], [
            bodyParam,
            outputFormatParam,
            includeParam,
            includeNameParam,
            offsetParam,
            limitParam,
            sortParam
        ]);
        return new operation(operationName, {
            method: "POST",
            baseUrl: "/query",
            dataType: getDataTypeFromRequest(parameters.body),
            contentType: getContentTypeFromRequest(parameters.body)
        }, settings, operationParameters, parameters, registryUrl);
    } else {
        // GET
        var operationParameters = $.extend(true, [], [
            $.extend(true, {}, requestParam, {
                value: operationName
            }),
            serviceParam,
            versionParam,
            propertyNameParam,
            parameterNameParam,
            outputFormatParam,
            includeParam,
            includeNameParam,
            offsetParam,
            limitParam,
            sortParam
        ]);
        return new operation(operationName, {
            method: "GET",
            baseUrl: "/query"
        }, settings, operationParameters, parameters, registryUrl);
    }
}

/** Describe a record (gets the schema definition). */
function describeRecord(parameters, settings, registryUrl) {
    var operationName = "DescribeRecord";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    // Substitute GET parameters into POST body
    updatePostParameters(parameters);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/query",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Publication Operations */
/* ========================================================================== */

/** Executes a registry Transaction. */
function transaction(parameters, settings, registryUrl) {
    var operationName = "Transaction";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        partsParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/publish",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/** Executes a registry Harvest. */
function harvest(parameters, settings, registryUrl) {
    var operationName = "Harvest";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/publish",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/** Executes a Galdos Common Service (GCS) Transaction. */
function gcsTransaction(parameters, settings, registryUrl) {
    var operationName = "GCS Transaction";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        partsParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/gcs",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Life-Cycle Management API */
/* ========================================================================== */

/** Executes a Life-Cycle Management (LCM) Transaction. */
function lcmTransaction(parameters, settings, registryUrl) {
    var operationName = "LCM Transaction";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/lcm",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* User Role Management API */
/* ========================================================================== */

/** Executes a User Role Management (URM) Transaction. */
function urmTransaction(parameters, settings, registryUrl) {
    var operationName = "URM Transaction";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/urm",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/** Executes a User Role Management (URM) validation request. */
function urmValidate(parameters, settings, registryUrl) {
    var operationName = "URM Validate";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/urm/validate",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/** Validates the given username (or id) and password against the URM Validate operation. */
function validateCredentials(parameters, settings, registryUrl) {
    var dfd = new $.Deferred();
    if ((parameters.id || parameters.username) && parameters.password) {
        var userContext = {
            usercontext: {
                password: parameters.password
            }
        };
        if (parameters.username) {
            userContext.usercontext.loginName = parameters.username;
        } else if (parameters.id) {
            userContext.usercontext.userID = parameters.id;
        }
        var params = {
            body: JSON.stringify(userContext),
            outputFormat: "application/json"
        };
        if (settings && settings.dataType) {
            delete settings.dataType;
        }
        $.when(urmValidate(params, settings, registryUrl)).done(function(data) {
            if (data.message == "Pass") {
                dfd.resolve(data);
            } else {
                dfd.reject(data, "error", "Supplied credentials are not valid.");
            }
        }).fail(function(jqXHR, textStatus, errorThrown) {
            dfd.reject(jqXHR, textStatus, errorThrown);
        });
    } else {
        dfd.reject(undefined, "error", "Must specify a username (or id) and password to validate.");
    }
    return dfd.promise();
}

/** Gets a registry user context for the given user. */
function getUserContext(parameters, settings, registryUrl) {
    var operationName = "GetUserContext";
    if (parameters && parameters.body) {
        // POST
        var operationParameters = $.extend(true, [], [
            bodyParam,
            $.extend(true, {}, requestParam, {
                value: operationName
            }),
            $.extend(true, {}, loginNameParam, {
                required: false
            }),
            userIdParam,
            outputFormatParam
        ]);
        return new operation(operationName, {
            method: "POST",
            baseUrl: "/query",
            dataType: getDataTypeFromRequest(parameters.body),
            contentType: getContentTypeFromRequest(parameters.body)
        }, settings, operationParameters, parameters, registryUrl);
    } else {
        // GET
        var operationParameters = $.extend(true, [], [
            $.extend(true, {}, requestParam, {
                value: operationName
            }),
            $.extend(true, {}, loginNameParam, {
                required: false
            }),
            userIdParam,
            outputFormatParam
        ]);
        return new operation(operationName, {
            method: "GET",
            baseUrl: "/query"
        }, settings, operationParameters, parameters, registryUrl);
    }
}

/** Gets all registry user contexts (requires appropriate credentials). */
function getAllUserContexts(parameters, settings, registryUrl) {
    var operationName = "GetAllUserContexts";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, requestParam, {
            value: operationName
        }),
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/query"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets all registry user roles. */
function getAllRoles(parameters, settings, registryUrl) {
    var operationName = "GetAllRoles";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, requestParam, {
            value: operationName
        }),
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/query"
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Bulk Retrieval API */
/* ========================================================================== */

/** Executes a Bulk Retrieval request. */
function bulkRetrieval(parameters, settings, registryUrl) {
    var operationName = "BulkRetrieval";
    if (parameters && parameters.body) {
        // POST
        var operationParameters = $.extend(true, [], [
            bodyParam
        ]);
        return new operation(operationName, {
            method: "POST",
            baseUrl: "/bulk",
            contentType: "application/xml;charset=UTF-8"
        }, settings, operationParameters, parameters, registryUrl);
    } else {
        // GET
        var operationParameters = $.extend(true, [], [
            qidParam,
            anyParam
        ]);
        return new operation(operationName, {
            method: "GET",
            baseUrl: "/bulk"
        }, settings, operationParameters, parameters, registryUrl);
    }
}

/* ========================================================================== */
/* Synchronize API */
/* ========================================================================== */

/** Executes a Synchronize request. */
function synchronize(parameters, settings, registryUrl) {
    var operationName = "Synchronize";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/synchronize",
        dataType: getDataTypeFromRequest(parameters.body),
        contentType: getContentTypeFromRequest(parameters.body)
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Audit API */
/* ========================================================================== */

/** Gets the audit history */
function getAuditHistory(parameters, settings, registryUrl) {
    var operationName = "GetAuditHistory";
    var operationParameters = $.extend(true, [], [
        anyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/audit"
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Extension Package Management (EPM) API */
/* ========================================================================== */

/** Executes a Extension Package Management (EPM) Transaction. */
function epmTransaction(parameters, settings, registryUrl) {
    var operationName = "EPM Transaction";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        partsParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/epm",
        contentType: "application/xml;charset=UTF-8"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Executes a Extension Package Management (EPM) validate request. */
function epmValidate(parameters, settings, registryUrl) {
    var operationName = "EPM Validate";
    var operationParameters = $.extend(true, [], [
        bodyParam,
        partsParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/epm/validate",
        contentType: "application/xml;charset=UTF-8"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Executes a Extension Package Management (EPM) model update request. */
function epmUpdate(parameters, settings, registryUrl) {
    var operationName = "EPM Model Update";
    var operationParameters = $.extend(true, [], [
        bodyParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/epm/model",
        contentType: "application/xml;charset=UTF-8"
    }, settings, operationParameters, parameters, registryUrl);
}

// TODO: Add EPM export GET method
/** Executes a Extension Package Management (EPM) Export Transaction. */
function epmExport(parameters, settings, registryUrl) {
    var operationName = "EPM Export";
    var operationParameters = $.extend(true, [], [
        bodyParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/epm/export",
        contentType: "application/xml;charset=UTF-8"
    }, settings, operationParameters, parameters, registryUrl);
}

// TODO: Add EPM export data GET method
/** Executes a Extension Package Management (EPM) Export Data Transaction. */
function epmExportData(parameters, settings, registryUrl) {
    var operationName = "EPM Data Export";
    var operationParameters = $.extend(true, [], [
        bodyParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/epm/exportinstancedata",
        contentType: "application/xml;charset=UTF-8"
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Management API */
/* ========================================================================== */

/** Take the registry online. */
function takeRegistryOnline(settings, registryUrl) {
    var operationName = "TakeRegistryOnline";
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/online"
    }, settings, null, null, registryUrl);
}

/** Take the registry offline. */
function takeRegistryOffline(settings, registryUrl) {
    var operationName = "TakeRegistryOffline";
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/offline"
    }, settings, null, null, registryUrl);
}

/** Refreshes the registry context. */
function refreshContext(settings, registryUrl) {
    var operationName = "RefreshContext";
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/refresh"
    }, settings, null, null, registryUrl);
}

/** Set the registry license key. */
function setLicenseKey(parameters, settings, registryUrl) {
    var operationName = "SetLicenseKey";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, keyParam, {
            encode: true
        })
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/license"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Set a registry property (name = value). */
function setProperty(parameters, settings, registryUrl) {
    var operationName = "SetProperty";
    var operationParameters = $.extend(true, [], [
        keyParam,
        valueParam,
        refreshParam,
        backupParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/set"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Take the registry offline, set the property, and take back online. */
function setPropertyFromOnline(parameters, settings, registryUrl) {
    var dfd = new $.Deferred();
    takeRegistryOffline(settings, registryUrl).done(function() {
        setProperty(parameters, settings, registryUrl).done(function() {
            takeRegistryOnline(settings, registryUrl).done(function(data) {
                dfd.resolve(data);
            }).fail(function(jqXHR, textStatus, errorThrown) {
                dfd.reject(jqXHR, textStatus, errorThrown);
            });
        }).fail(function(jqXHR, textStatus, errorThrown) {
            dfd.reject(jqXHR, textStatus, errorThrown);
        });
    }).fail(function(jqXHR, textStatus, errorThrown) {
        dfd.reject(jqXHR, textStatus, errorThrown);
    });
    return dfd.promise();
}

/** Set the registry database connection properties. */
function setDatabaseConnection(parameters, settings, registryUrl) {
    var operationName = "SetDatabaseConnection";
    var operationParameters = $.extend(true, [], [
        driverClassParam,
        jdbcUrlParam,
        userParam,
        passwordParam
    ]);
    // Allow for driver class approximations
    if (parameters.driverClass == "postgresql") {
        parameters.driverClass = "org.postgresql.Driver";
    } else if (parameters.driverClass == "oracle") {
        parameters.driverClass = "oracle.jdbc.driver.OracleDriver";
    }
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/dbconfig"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Create the registry database. */
function createDatabase(settings, registryUrl) {
    var operationName = "CreateDatabase";
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/dbcreate"
    }, settings, null, null, registryUrl);
}

/** Create an initial registry admin user. */
function createAdminUser(parameters, settings, registryUrl) {
    var operationName = "CreateAdminUser";
    var operationParameters = $.extend(true, [], [
        loginNameParam,
        passwordParam,
        firstNameParam,
        lastNameParam,
        email1Param,
        email1TypeParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/admincreate"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Load the registry packages (CSW-ebRIM Basic and INdicio packages). */
function loadPackages(settings, registryUrl) {
    var operationName = "LoadPackages";
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/load"
    }, settings, null, null, registryUrl);
}

/** Get the registry state. */
function getRegistryState(parameters, settings, registryUrl) {
    var operationName = "GetRegistryState";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/getstate"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Get a registry property by name. */
function getProperty(parameters, settings, registryUrl) {
    var operationName = "GetProperty";
    var operationParameters = $.extend(true, [], [
        keyParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/get"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets all registry properties. */
function getAllProperties(parameters, settings, registryUrl) {
    var operationName = "GetAllProperties";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/management/getall"
    }, settings, operationParameters, parameters, registryUrl);
}

/* ========================================================================== */
/* Diagnostics API */
/* ========================================================================== */

/** Gets the product metadata (version, build id, build time, etc). */
function getProductMetadata(parameters, settings, registryUrl) {
    var operationName = "GetProductMetadata";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/diagnostics/metadata"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Tests the current (if no parameters specified) or given database connection,
and returns the database connection parameters. */
function testDatabaseConnection(parameters, settings, registryUrl) {
    var operationName = "TestDatabaseConnection";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, driverClassParam, {
            required: false
        }),
        $.extend(true, {}, jdbcUrlParam, {
            required: false
        }),
        $.extend(true, {}, userParam, {
            required: false
        }),
        $.extend(true, {}, passwordParam, {
            required: false
        })
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/dbconnection"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Refreshes the database statistics for the registry schema. */
function refreshDatabaseStatistics(parameters, settings, registryUrl) {
    var operationName = "RefreshDatabaseStatistics";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/dbstats"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a list of the system properties (servlet, build, and system). */
function getSystemProperties(parameters, settings, registryUrl) {
    var operationName = "GetSystemProperties";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/system"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a list of the application threads. */
function getApplicationThreads(parameters, settings, registryUrl) {
    var operationName = "GetApplicationThreads";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/threads"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets the current log level. */
function getLogLevel(parameters, settings, registryUrl) {
    var operationName = "GetLogLevel";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/loglevel"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Sets the log level (and also returns the set level). */
function setLogLevel(parameters, settings, registryUrl) {
    var operationName = "SetLogLevel";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, levelParam, {
            required: false
        }),
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/loglevel"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a list of all registry log files. */
function getLogFiles(parameters, settings, registryUrl) {
    var operationName = "GetLogFiles";
    var operationParameters = $.extend(true, [], [
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "POST",
        baseUrl: "/diagnostics/logfiles"
    }, settings, operationParameters, parameters, registryUrl);
}

/** Gets a single registry log file by name (zipped). */
function getLogFile(parameters, settings, registryUrl) {
    var operationName = "GetLogFile";
    var operationParameters = $.extend(true, [], [
        $.extend(true, {}, filenameParam, {
            required: false
        }),
        outputTypeParam,
        outputFormatParam
    ]);
    return new operation(operationName, {
        method: "GET",
        baseUrl: "/diagnostics/logfiles"
    }, settings, operationParameters, parameters, registryUrl);
}