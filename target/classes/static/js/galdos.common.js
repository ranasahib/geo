/**
 * Library of common Galdos functions.
 *
 * @requires jQuery <https://jquery.com/>
 */

/** Sets debug logging on or off (console.log; does not work in IE). */
var DEBUG = false;

/** Common XML namespaces, indexed by prefix. */
var namespaces = {
    "rim": "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0",
    "csw": "http://www.opengis.net/cat/csw/2.0.2",
    "wrs": "http://www.opengis.net/cat/wrs/1.0",
    "gml": "http://www.opengis.net/gml/3.2",
    "xlink": "http://www.w3.org/1999/xlink"
};

/* ========================================================================== */
/* Common */
/* ========================================================================== */

/** Returns the parameter value, if set, otherwise return the defaultValue.
This is required for boolean or number parameters; otherwise use the
"param = param || defaultValue" syntax. */
function defaultParam(param, defaultValue) {
    return typeof param === "undefined" ? defaultValue : param;
}

/** Gets the last component of an id.  e.g. "urn:uuid:Building:001" would return
"001" (with the default delimiter). */
function getShortName(name, delimiter) {
    delimiter = defaultParam(delimiter, ":");
    var shortName = name.split(delimiter);
    return shortName[shortName.length-1];
}

/** Converts all groups of spaces into single spaces. */
function normalizeSpaces(str) {
    return str.replace(/\s+/gi, " ");
}

/** Removes all spaces in a string. */
function removeSpaces(str) {
    return str.replace(/\s+/gi, "");
}

/** Removes non-id characters in a string (space, forward slash, back slash,
etc). */
function removeIdChars(str) {
    return str.replace(/[\s+\/\\]/gi, "");
}

/** Escapes periods and colons so that an ID can be used in a jQuery or CSS
selector. */
function escapeId(id) {
    return id.replace(/\./gi, "\\.").replace(/\:/gi, "\\:");
}

/** Gets URN values from a string and returns the list of matches. */
function getUrn(str) {
    return str.match(/urn:[\w\d:\-_]+/g);
}

/** Checks to see if a string starts with a certain prefix. */
function startsWith(str, prefix) {
    return str.indexOf(prefix) == 0;
}

/** Checks to see if a string ends with a certain suffix. */
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

/** Returns true if the given value is a valid date string; e.g. 2012-10-10. */
function isDateString(value) {
    var result = value.match(/^[\d]{4}-[\d]{2}-[\d]{2}$/);
    if (result == null) {
        return false;
    }
    return result;
}

/** Returns true if the given value is a valid hex code; e.g. FFFFFF or #333.
*/
function isHexCodeString(value) {
    var result = value.match(/^#?([0-9a-fA-F]{3})([0-9a-fA-F]{3})?$/);
    if (result == null) {
        return false;
    }
    return result;
}

/** Returns true if the given value is a valid coordinate tuple; e.g.
49.6 -123.2 or 10.2,160.3. */
function isCoordinateString(value) {
    var result = value.match(/^-?\d+(.\d+)?(\s+|\s*,\s*)-?\d+(.\d+)?$/);
    if (result == null) {
        return false;
    }
    return result;
}

/** Returns true if the given value is a valid alphanumeric string (with some
punctuation; hyphen or underscore); e.g. 1a-2b_3c. */
function isAlphaNumericString(value) {
    var result = value.match(/^[0-9a-zA-Z_-]+$/i);
    if (result == null) {
        return false;
    }
    return result;
}

/** Returns true if the given value is a valid URN (starts with urn: and
contains at least 2 parts after the first colon, where each part is
colon-separated and cannot be empty); e.g. urn:namespace-part:id-part. */
function isUrn(value) {
    var result = value.match(/^urn:.+:.+$/i);
    if (result == null) {
        return false;
    }
    return result;
}

/** Returns true if the given value is a valid base64 encoded string */
function isBase64(value) {
    var result = value.match("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    if (result == null) {
        return false;
    }
    return result;
}

/** Checks if a given value is a boolean. */
function isBoolean(value) {
    return typeof value === "boolean"
        || (/^(true|false)$/i).test(value);
}

/** Checks if a given value is an integer. */
function isInteger(value) {
    return !isNaN(parseInt(value));
}

/** Checks if a given value is a mimetype string (e.g. application/xml or image/*). */
function isMimeType(value) {
    return (/^[-\w]+\/([-\w]+|\*)$/).test(value);
}

/** Checks if a given value is a language string (e.g. en or en-US). */
function isLanguage(value) {
    return (/^[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*$/).test(value);
}

/** Ensures the length of the given value is within the max and min (optional) bounds. */
function isWithinBounds(value, max, min) {
    if (typeof min !== "undefined") {
        return (value.length <= max) && (value.length >= min);
    } else {
        return (value.length <= max);
    }
}

/** Appends a URL to a base URL.  Handles URL to ensure a double slash is not
included (can cause issues with browser form submission or history). */
function appendUrl(base, url) {
    if (typeof base === "undefined" || base === null || base === "") {
        return url;
    }
    var baseSlash = base.slice(-1) === "/";
    var urlSlash = url.charAt(0) === "/";
    if (baseSlash && urlSlash) {
        return base.slice(0, base.length-1) + url;
    } else if (!baseSlash && !urlSlash) {
        return base + "/" + url;
    } else {
        return base + url;
    }
}

/** Returns the list of KVP parameters in the current window URL. */
function getKvpParams() {
    var params = {};
    window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi,
        function(m, key, value) {
            params[key] = value;
        });
    return params;
}

/** Adds a KVP name-value pair to an existing string and returns the new updated
value (e.g. name1=value1&name2=value2).  Set includeEmpty to true if empty
values are desired. */
function addKvpValue(str, name, value, includeEmpty) {
    includeEmpty = defaultParam(includeEmpty, false);
    // Only add valid values
    if (typeof value !== "undefined"
        && (includeEmpty || (!includeEmpty && value != ""))) {
        if (str != "") {
            str += "&";
        }
        str += name + "=" + value;
    }
    return str;
}

/** Creates a KVP string from a set of parameters (param1=value1&...).  Note
that the value is also URL encoded by default. */
function createKvpString(params, encode) {
    encode = defaultParam(encode, true);
    var queryString = "";
    if (params) {
        $.each(params, function(i, n) {
            queryString += "&" + i + "=" + n.replace(/\s/g, "+");
        });
    }
    return encode ? encodeURIComponent(queryString) : queryString;
}

/** Converts a KVP string into an object with key value pairs. */
function kvpToParams(kvpString, decode) {
    decode = defaultParam(decode, true);
    var params = {};
    var regex = new RegExp(/\??(.*?)=(.*?)[&|$]/g);
    while (match = regex.exec(kvpString)) {
        if (match.length > 2) {
            params[match[1]] = decodeURIComponent(match[2]);
        }
    }
    return params;
}

/** Replaces all parameters in a string.  Expects string parameters to be of the
syntax "${ paramName }".  Params specifies the name and replacement value; e.g.:
{ "name": "my name" } */
function replaceParams(str, params) {
    var newStr = str;
    $.each(params, function(name, value) {
        var regex = new RegExp("\\$\\{\\s*" + name + "\\s*\\}", "gm");
        newStr = newStr.replace(regex, value);
    });
    return newStr;
}

/** Splits a string on the first instance of another string, and returns the two
concatenated components. */
function splitOnce(str, match) {
    var components = str.split(match);
    var arr = [];
    arr[0] = components.shift();
    arr[1] = components.join(match);
    return arr;
}

/** Sorts the given select list alphabetically, by option groups and option
text.  After sorting, the first option is selected by default (selectFirst). */
function sortGroupList(select, options) {
    options = options || {};
    options.selectFirst = typeof options.selectFirst !== "undefined" ? options.selectFirst : true;
    var groups = $(select).find("optgroup");
    if (groups && groups.length > 0) {
        $.each(groups, function(idx, group) {
            sortSelect(group, $.extend({}, options, {selectFirst: false}));
        });
        sortSelect(select, $.extend({}, options, {type: "optgroup", attr: "label", selectFirst: false}));
        if (options.selectFirst) {
            $(select).val($("option:first", select).val()).change;
        }
    } else {
        sortSelect(select, options);
    }
}

/** Sorts a select element by option text. Specify sort as ASC (default) or
DESC; selectFirst as true (default) or false); attr to sort on an attribute
value or data to sort on a data value. */
function sortSelect(select, options) {
    options = options || {};
    options.selectFirst = typeof options.selectFirst !== "undefined" ? options.selectFirst : true;
    options.type = options.type || "option";
    var opts = $(select).find(options.type);
    opts.sort(function(a, b) {
        // Text (default)
        var aVal = $(a).text();
        var bVal = $(b).text();
        if (options.attr) {
            // Attribute
            aVal = $(a).attr(options.attr);
            bVal = $(b).attr(options.attr);
        } else if (options.data) {
            // Data
            aVal = $(a).data(options.data);
            bVal = $(b).data(options.data);
        }
        // ASC (default) or DESC order
        if (options.order && options.order.toUpperCase() == "DESC") {
            return aVal == bVal ? 0 : aVal > bVal ? -1 : 1;
        } else {
            return aVal == bVal ? 0 : aVal < bVal ? -1 : 1;
        }
    });
    $(select).empty().append($(opts));
    // Select the first option
    if (options.selectFirst) {
        $(select).val($(select).find("option:first").val()).change();
    }
}

/** Sort an array of elements alphabetically (by text). */
function sortByText(options) {
    options.sort(function(a, b) {
        return $(a).text() == $(b).text() ? 0 : $(a).text() < $(b).text() ? -1 : 1;
    });
}

/** Sort an array of elements alphabetically (by attribute name; defaults to
value, if not specified). */
function sortByAttr(options, attrName) {
    attrName = defaultParam(attrName, "value");
    options.sort(function(a, b) {
        return $(a).attr(attrName) == $(b).attr(attrName) ?
            0 : $(a).attr(attrName) < $(b).attr(attrName) ? -1 : 1;
    });
}

/** Sort an array of elements alphabetically by a data attribute. */
function sortByData(options, dataName) {
    options.sort(function(a, b) {
        return $(a).data(dataName) == $(b).data(dataName) ?
            0 : $(a).data(dataName) < $(b).data(dataName) ? -1 : 1;
    });
}

/** Counts the number of properties in a given object. */
function countProperties(obj) {
    var count = 0;
    for (var prop in obj) {
        if (obj.hasOwnProperty(prop)) {
            ++count;
         }
    }
    return count;
}

/** Converts a lower camel-case string to a space-delimited upper camel-case string. */
function prettify(str) {
    return str
        .replace(/^([a-z])/, function(m, p1) {
            return p1.toUpperCase();
        })
        .replace(/([a-z1-9])([A-Z])/g, "$1 $2")
        .replace(/([A-Z])([A-Z])([a-z])/g, "$1 $2$3");
}

/** Capitalizes the first letter in a string. */
function capitalize(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

/** Capitalizes all starting characters in a string (beginning of word
boundary). */
function capitalizeAll(string) {
    return string.replace(/(\b[a-z])/g, function(m, p1) {
        return p1.toUpperCase();
    });
}

/** Truncates a long text field to the given number of characters (including the
suffix "...").  Length defaults to 25. */
function truncateText(text, length) {
    length = defaultParam(length, 25);
    // Ensure the length is a proper value
    length = Math.abs(length);
    length = length <= 3 ? length + 3 : length;
    if (text.length > length) {
        text = text.substring(0, length - 3) + "...";
    }
    return text;
}

/** Truncates a float to the given number of decimal places (defaults to 5). */
function truncateFloat(flt, places) {
    places = defaultParam(places, 5);
    return flt.toFixed(places);
}

/** Truncates a number string to the given number of decimal places (defaults
to 5). */
function truncateNumberString(string, places) {
    places = defaultParam(places, 5);
    return parseFloat(string).toFixed(places);
}

/** Generates a RFC 4122 UUID value. */
function generateUuid() {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0;
        var v = c == "x" ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

/** Generates a RFC 4122 UUID URN ("urn:uuid:..."). */
function generateUuidUrn() {
    return "urn:uuid:" + generateUuid();
}

/** Generates a random boundary character sequence (defaults to 16 characters). */
function generateBoundary(length) {
    length = defaultParam(length, 16);
    var fill = new Array(length + 1).join("x");
    return fill.replace(/[x]/g, function(c) {
        var v = Math.random()*16|0;
        return v.toString(16);
    });
}

/* ========================================================================== */
/* AJAX */
/* ========================================================================== */

/** Sets the given header to the given value on the AJAX object. */
function setHeader(jqXHR, name, value) {
    jqXHR.setRequestHeader(name, value);
}

/** Sets accept-language value on the AJAX object. */
function setAcceptLanguage(jqXHR, lang) {
    jqXHR.setRequestHeader("Accept-Language", lang);
}

/** Sets authorization credentials to the AJAX object. */
function setAuthentication(jqXHR, username, password) {
    jqXHR.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
}

/** Sets authorization credentials to the AJAX object, taken from global username and password properties. */
function setGlobalAuthentication(jqXHR) {
    if (typeof username !== "undefined" && typeof password !== "undefined") {
        jqXHR.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
    }
}

/** Clears the global username and password properties. */
function clearGlobalAuthentication() {
    username = undefined;
    password = undefined;
}

/** Displays the AJAX success response in the console. */
function successCallback(data, textStatus, jqXHR) {
    if (DEBUG) {
        console.log("Success:");
        console.log(data);
    }
}

/** Displays the AJAX error response in the console. */
function errorCallback(jqXHR, textStatus, errorThrown) {
    if (DEBUG) {
        console.log("Error (" + textStatus + "):");
        console.log(errorThrown);
        if (typeof jqXHR !== "undefined" && typeof jqXHR.responseText !== "undefined") {
            console.log(jqXHR.responseText);
        } else {
            console.log(jqXHR);
        }
    }
}

/**
    Chains the array of functions in order (each function is assumed to return
    a promise).  For example:

    var promises = [
        function() { return takeRegistryOffline(); },
        function() { return setProperty("my.property", "123"); },
        function() { return takeRegistryOnline(); }
    ];
    chainPromises(promises).done(successCallback).fail(errorCallback);
*/
function chainPromises(promises) {
    var promise = promises[0];
    return promise.call().done(function() {
        promises.shift();
        if (promises.length != 0) {
            return chainPromises(promises);
        }
    });
}

/** The cache that holds promises. */
var promises = {};

/** Caches a given promise by key. */
function cache(key, promise, refresh) {
    if (typeof refresh !== "undefined" && refresh) {
        delete promises[key];
    }
    var p = promises[key];
    if (p) {
        if (DEBUG) {
            console.log("Cache hit for key: " + key);
        }
        return p;
    } else {
        promises[key] = promise;
        return promise;
    }
}

/** Removes a given promise from the cache, by key. */
function removeCacheKey(key) {
    delete promises[key];
}

/** Clears the cache. */
function clearCache() {
    promises = {};
}

/** Converts an array buffer into a base64 string. */
function arrayBufferToBase64(buffer) {
    // Note that btoa(String.fromCharCode.apply(null, arrayBuffer)) will not work for large arrays
    var binary = "";
    var bytes = new Uint8Array(buffer);
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary);
}

/** Reads a file (elem.files[0]) into a base64 string. */
function readFileAsBase64(file) {
    var dfd = new $.Deferred();
    var reader = new FileReader();
    reader.onload = function(f) {
        dfd.resolve(arrayBufferToBase64(new Uint8Array(f.target.result)));
    };
    reader.onerror = function() {
        dfd.reject(this, "Error", "Unable to read file");
    };
    if (typeof file === "undefined") {
        dfd.reject(this, "Error", "No file specified");
    } else {
        reader.readAsArrayBuffer(file);
    }
    return dfd.promise();
}

/** Reads any number of files (readFileAsBase64), using a jQuery selector, into
a multipart parts object. */
function readAllFilesAsBase64(selector) {
    var d = new $.Deferred();
    var promises = [];
    var parts = [];
    $(selector).each(function(i, n) {
        var file = n.files[0];
        var promise = $.when(readFileAsBase64(file)).done(function(result) {
            parts.push({
                id: file.name,
                mimetype: file.type,
                body: result
            });
        });
        promises.push(promise);
    });
    $.when.apply($, promises).done(function() {
        d.resolve(parts);
    }).fail(function(jqXHR, textStatus, errorThrown) {
        d.reject(jqXHR, textStatus, errorThrown);
    });
    return d.promise();
}

/** Creates a new multipart-related HTTP request, and returns a promise.  Uses
the CID scheme to reference parts (array of objects that contain an id,
mimetype, and body).  The body is always sent as base64.  The default body
mimetype is application/xml. */
function multipartRelated(url, requestBody, mimetype, parts, domain) {
    mimetype = mimetype || "application/xml";
    domain = typeof domain !== "undefined" ? domain : false;
    var dfd = new $.Deferred();
    var xhr = new XMLHttpRequest();
    var method = "POST";
    var boundary = generateBoundary();
    var uuid = generateUuid();
    var request = "--" + boundary + "\n"
        + "Content-ID: <" + uuid + (domain ? "@" + domain : "") + ">\n"
        + "Content-Type: " + mimetype + "\n\n"
        + requestBody;
    if (typeof parts !== "undefined") {
        // Ensure the required parts are all present
        var regex = /mimeType=["'](.*?)["'][\S\s]*?:href=["']cid:(.*?)(@(.*?))?["']/g;
        var match = regex.exec(requestBody);
        while (match != null) {
            var localMimetype = match[1];
            var id = match[2];
            var hasPart = false;
            $.each(parts, function(idx, p) {
                if (p.id) {
                    // Ignore domain, if specified
                    var altId = p.id.indexOf("@") > -1
                        ? p.id.substr(0, p.id.indexOf("@")) : p.id + (domain ? "@" + domain : "");
                    if (id === p.id || id === altId) {
                        hasPart = true;
                        // Provide the mimetype from the request, if not given
                        if (!p.mimetype) {
                            p.mimetype = localMimetype;
                        }
                    }
                }
            });
            if (!hasPart) {
                var msg = "Missing required part: " + id;
                if (DEBUG) {
                    console.log(msg);
                }
                dfd.reject(xhr, "Error", msg);
            }
            match = regex.exec(requestBody);
        }
        $.each(parts, function(idx, p) {
            // Ensure each part has the required properties
            if (!p.id || !p.mimetype || !p.body) {
                var msg = "Part missing required id, mimetype, or body";
                if (DEBUG) {
                    console.log(msg);
                }
                dfd.reject(xhr, "Error", msg);
            }
            request += "\n--" + boundary + "\n";
            var id = p.id.indexOf("@") > -1 ? p.id : p.id + (domain ? "@" + domain : "");
            request += "Content-ID: <" + id + ">\n";
            // Always send base64 since some parts may be binary
            request += "Content-Transfer-Encoding: base64\n";
            request += "Content-Type: " + p.mimetype + "\n\n";
            request += p.body;
        });
    }
    request += "\n--" + boundary + "--\n";
    // Create the HTML request as multipart/related
    xhr.open(method, url, true);
    xhr.setRequestHeader("Content-Type",
        "multipart/related; boundary=\"" + boundary + "\"; type=\"" + mimetype + "\"");
    // Apply ajax settings as best as possible
    if (registryDefaults.ajaxSettings) {
        if (registryDefaults.ajaxSettings.beforeSend) {
            registryDefaults.ajaxSettings.beforeSend.call(this, xhr);
        }
    }
    xhr.onload = function() {
        if (xhr.status == 200) {
            dfd.resolve(xhr.response, xhr.statusText, xhr);
        } else {
            dfd.reject(xhr, "Error", xhr.statusText);
        }
    };
    xhr.onerror = function() {
        dfd.reject(xhr, "Network Error", xhr.statusText);
    };
    xhr.send(request);
    return dfd.promise();
}

/* ========================================================================== */
/* JSON */
/* ========================================================================== */

/** Determines if the given object is a JSON string. */
function isJsonString(obj) {
    return (typeof obj === "string" && obj.match(/^\{[\s\S]*\}$/g));
}

/** Determines if the given object is a JSON object (must be an array or plain object). */
function isJsonObject(obj) {
    //return (typeof obj === "object" && obj != null && !$.isXMLDoc(obj));
    return ($.isArray(obj) || $.isPlainObject(obj));
}

/** Determines if the given object is JSON (string or object). */
function isJson(obj) {
    return (isJsonString(obj) || isJsonObject(obj));
}

/* ========================================================================== */
/* XML */
/* ========================================================================== */

/** Converts an XML entity into a String. */
function xmlToString(xml) {
    var xmlString;
    if (window.ActiveXObject) {
        xmlString = xml.xml;
    } else {
        xmlString = (new XMLSerializer()).serializeToString(xml);
    }
    return xmlString;
}

/** Transforms the given source document with the given stylesheet (uses an XML
identify transform with indentation if no stylehseet provided). */
function transform(source, stylesheet) {
    if (typeof stylesheet === "undefined") {
        stylesheet = $.parseXML('<xsl:stylesheet version="2.0"'
            + '    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"'
            + '    xmlns:xsd="http://www.w3.org/2001/XMLSchema"'
            + '    exclude-result-prefixes="xsl xsd">'
            + '    <xsl:output method="xml" indent="yes" omit-xml-declaration="no" />'
            + '    <xsl:preserve-space elements="*" />'
            + '    <xsl:template match="@*|node()">'
            + '        <xsl:copy>'
            + '            <xsl:apply-templates select="@*|node()" />'
            + '        </xsl:copy>'
            + '    </xsl:template>'
            + '</xsl:stylesheet>');
    }
    if (window.ActiveXObject) {
        return source.transformNode(stylesheet);
    } else if (document.implementation && document.implementation.createDocument) {
        var xslt = new XSLTProcessor();
        xslt.importStylesheet(stylesheet);
        return xslt.transformToFragment(source, document);
    }
}

/** Creates an array of spaces and newlines; used by formatXml for indenting. */
function createShiftArr(step) {
    var space = "";
    if (isNaN(parseInt(step))) { // argument is string
        space = step;
    } else { // argument is integer
        for (i = 0; i < step; i++) {
            space += " ";
        }
    }
    var shift = ["\n"]; // array of shifts
    for (ix = 0; ix < 100; ix++){
        shift.push(shift[ix]+space);
    }
    return shift;
}

/** Formats (indents) XML content. */
function formatXml(text, step) {
    step = typeof step === "undefined" ? "    " : step; // 4 spaces
    text = $.isXMLDoc(text) ? xmlToString(text) : text;
    var ar = text.replace(/>\s{0,}</g, "><")
                 .replace(/</g, "~::~<")
                 .replace(/\s*(\w+?\=['"])/g, "~::~$1")
                 .replace(/\s*(\w+?):~::~/g, "~::~$1:")
                 .split("~::~");
    var len = ar.length;
    var inComment = false;
    var str = "";
    var attr = 0;
    var deep = 0;
    var ix = 0;
    var shift = createShiftArr(step);

    for (ix = 0; ix < len; ix++) {
        // opening comment/cdata/doctype/processing instruction
        if (ar[ix].search(/<!/) > -1 || ar[ix].search(/<\?/) > -1) {
            if (DEBUG) {
                console.log("opening comment", ar[ix]);
            }
            str += shift[deep]+ar[ix];
            inComment = true;
            // end comment/cdata/doctype/processing instruction
            if (ar[ix].search(/-->/) > -1 || ar[ix].search(/\?>/) > -1 || ar[ix].search(/\]>/) > -1 || ar[ix].search(/!DOCTYPE/) > -1) {
                if (DEBUG) {
                    console.log("end comment", ar[ix]);
                }
                inComment = false;
            }
        }
        // closing comment/cdata/doctype/processing instruction
        else if (ar[ix].search(/-->/) > -1 || ar[ix].search(/\?>/) > -1 || ar[ix].search(/\]>/) > -1 || ar[ix].search(/!DOCTYPE/) > -1) {
            if (DEBUG) {
                console.log("closing comment", ar[ix]);
            }
            str += " "+ar[ix];
            inComment = false;
        }
        // closed elements, with no content
        else if (/^<\w/.exec(ar[ix-1]) && /^<\/\w/.exec(ar[ix]) &&
            /^<[\w:\-\.\,]+/.exec(ar[ix-1]) == /^<\/[\w:\-\.\,]+/.exec(ar[ix])[0].replace("/", "")) {
            if (DEBUG) {
                console.log("closing element (no content)", ar[ix]);
            }
            str += ar[ix];
            if (!inComment) {
                deep--;
                if (deep < 0) {
                    deep = 0;
                }
            }
        }
        // opening elements
        else if (ar[ix].search(/<\w/) > -1 && ar[ix].search(/<\//) == -1 && ar[ix].search(/\/>/) == -1) {
            if (DEBUG) {
                console.log("opening element", ar[ix]);
            }
            str = !inComment ? str += shift[deep++]+ar[ix] : str += ar[ix];
            attr = 0;
        }
        // closed elements, with content
        else if (ar[ix].search(/<\w/) > -1 && ar[ix].search(/<\//) > -1) {
            if (DEBUG) {
                console.log("closing element (with content)", ar[ix]);
            }
            str = !inComment ? str += shift[deep]+ar[ix] : str += ar[ix];
        }
        // closing elements
        else if (ar[ix].search(/<\//) > -1) {
            if (DEBUG) {
                console.log("closing element", ar[ix]);
            }
            deep--;
            if (deep < 0) {
                deep = 0;
            }
            str = !inComment ? str += shift[deep]+ar[ix] : str += ar[ix];
        }
        // self-closing elements
        else if (ar[ix].search(/\/>/) > -1) {
            if (DEBUG) {
                console.log("self-closing element", ar[ix]);
            }
            var noAttr = ar[ix].search(/^</) > -1;
            if (noAttr) {
                str = !inComment ? str += shift[deep]+ar[ix] : str += " "+ar[ix];
            } else {
                // un-nest for self-closed elements
                str = !inComment && attr > 0 ? str += shift[deep]+ar[ix] : str += " "+ar[ix];
                deep--;
                if (deep < 0) {
                    deep = 0;
                }
            }
        }
        // attributes/namespaces
        else if (ar[ix].search(/\w+?(\:\w+?)?\=['"]/) > -1) {
            if (DEBUG) {
                console.log("attribute", ar[ix]);
            }
            // keep the first attribute on the same line
            // do not expand nested attributes (i.e. Slot references)
            if (ix-1 > 0 && ar[ix-1].indexOf("Slot[@") !== -1) {
                str += ar[ix];
            } else {
                str = !inComment && attr > 0 ? str += shift[deep]+ar[ix] : str += " "+ar[ix];
                attr++;
            }
        }
        // all other content
        else {
            if (DEBUG) {
                console.log("other", ar[ix]);
            }
            str += ar[ix];
        }
    }
    return (str[0] == "\n") ? str.slice(1) : str;
}

/** These XML filters can take a single string, or multiple values as an array
for each parameter (namespace URI or local name).  Filters are applied to a DOM
element or result set, for example:
$(sampleXml).lnFilter("name"); */
$.fn.extend({
    /** Filters XML elements based on their namespace URI and local name. */
    nsFilter: function(namespaceURI, localName) {
            return $(this).find("*").filter(function() {
                var domnode = $(this)[0];
                var name = domnode.localName || domnode.baseName;
                var namespace = domnode.namespaceURI;
                return ($.isArray(namespaceURI) ?
                    $.inArray(namespace, namespaceURI) != -1 :
                        namespace === namespaceURI)
                && ($.isArray(localName) ? $.inArray(name, localName) != -1 :
                    name === localName);
        });
    },
    /** Filters XML elements based on their local name (ignores namespaces). */
    lnFilter: function(localName) {
        return $(this).find("*").filter(function() {
            var domnode = $(this)[0];
            var name = domnode.localName || domnode.baseName;
            return $.isArray(localName) ? $.inArray(name, localName) != -1 :
                name === localName;
        });
    }
});