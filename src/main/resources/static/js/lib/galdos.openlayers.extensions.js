$(function() {
    // New writers
    OpenLayers.Format.Filter.v1_1_0.prototype.filterMap.TextSearch          = "TextSearch";
    OpenLayers.Format.Filter.v1_1_0.prototype.filterMap.PropertyIsTrue      = "PropertyIsTrue";

    // New constants
    OpenLayers.Filter.Comparison.TEXT_SEARCH                                = "TextSearch";
    OpenLayers.Filter.Comparison.IS_TRUE                                    = "PropertyIsTrue";

    // Add the INdicio namespace
    OpenLayers.Format.Filter.v1_1_0.prototype.namespaces.idx                = "urn:x-galdosinc:indicio:web-registry-service";

    // Add an INdicio writing structure
    OpenLayers.Format.Filter.v1_1_0.prototype.writers.idx = {};

    /**
        OpenLayers.Filter.Comparison.TEXT_SEARCH
        ----------------------------------------

        Properties
            type                {String} the type of the comparison
            scope               {String} the scope to search
            terms               {String} the terms to match

        Example usage:
            var filter = new OpenLayers.Filter.Comparison({
                type: OpenLayers.Filter.Comparison.TEXT_SEARCH,
                scope: "RegistryObject",
                terms: "someTerm"
            });
    */
    OpenLayers.Format.Filter.v1_1_0.prototype.writers.ogc.TextSearch = function(filter) {
        var node = document.createElementNS(this.namespaces.idx, "idx:TextSearch");

        var scopeLiteral = document.createElementNS(this.namespaces.ogc, "ogc:Literal");
        var scopeElement = document.createElementNS(this.namespaces.idx, "idx:Scope");
        var scopeContent = document.createTextNode(filter.scope);
        scopeElement.appendChild(scopeContent);
        scopeLiteral.appendChild(scopeElement);

        var termsLiteral = document.createElementNS(this.namespaces.ogc, "ogc:Literal");
        var termsElement = document.createElementNS(this.namespaces.idx, "idx:Terms");
        var termsContent = document.createTextNode(filter.terms);
        termsElement.appendChild(termsContent);
        termsLiteral.appendChild(termsElement);

        node.appendChild(scopeLiteral);
        node.appendChild(termsLiteral);

        return node;
    };

    /**
        OpenLayers.Filter.Comparison.PropertyIsTrue
        -------------------------------------------

        Properties
            type                {String} the type of the comparison
            value               {Number} or {String} comparison value, typically a function

        Example usage:
            var filter = new OpenLayers.Filter.Comparison({
                type: OpenLayers.Filter.Comparison.PropertyIsTrue,
                value: ${OpenLayers Function}
            });
    */
    OpenLayers.Format.Filter.v1_1_0.prototype.writers.ogc.PropertyIsTrue = function(filter) {
        var node = this.createElementNS(this.namespaces.ogc, "ogc:PropertyIsTrue");
        this.writeOgcExpression(filter.value, node);
        return node;
    };

    /**
        OpenLayers.Filter.Function
        -------------------------------------------
        Note: extended the default OpenLayers.Filter.Function to accept any number of
        <ogc:PropertyName> elements

        Properties
            name                {String} name of the function
            params              {Array (OpenLayers.Filter.Function || String || Number )} function parameters
            properties          {Array String} array of property names

        Example usage:
            var filter = new OpenLayers.Filter.Function({
                name: "indicio:ClassifiedBy",
                params: [ "cnodeId" ],
                properties: [ "rim:RegistryObject/@id" ]
            });
    */
    OpenLayers.Format.Filter.v1_1_0.prototype.writers.ogc.Function = function(filter) {
        var node = this.createElementNSPlus("ogc:Function", {
            attributes: {
                name: filter.name
            }
        });

        /** added */
        var propertyNames = filter.properties;
        for(var i=0, len=propertyNames.length; i<len; i++) {
            var propNode = this.createElementNSPlus("ogc:PropertyName", {
                value: propertyNames[i]
            });
            node.appendChild(propNode);
        }

        var params = filter.params;
        for(var i=0, len=params.length; i<len; i++){
            this.writeOgcExpression(params[i], node);
        }
        return node;
    };

    // Add namespaces to the GetRecords request
    OpenLayers.Format.CSWGetRecords.v2_0_2.prototype.namespaces.rim = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0";
    OpenLayers.Format.CSWGetRecords.v2_0_2.prototype.namespaces.wrs = "http://www.opengis.net/cat/wrs/1.0";
    OpenLayers.Format.CSWGetRecords.v2_0_2.prototype.namespaces.gml = "http://www.opengis.net/gml/3.2";
    OpenLayers.Format.CSWGetRecords.v2_0_2.prototype.namespaces.ogc = "http://www.opengis.net/ogc";
    OpenLayers.Format.CSWGetRecords.v2_0_2.prototype.write = function(options) {
        var node = this.writeNode("csw:GetRecords", options);
        node.setAttribute("xmlns:rim", this.namespaces.rim);
        node.setAttribute("xmlns:wrs", this.namespaces.wrs);
        node.setAttribute("xmlns:gml", this.namespaces.gml);
        node.setAttribute("xmlns:ogc", this.namespaces.ogc);
        return OpenLayers.Format.XML.prototype.write.apply(this, [node]);
    }

});
