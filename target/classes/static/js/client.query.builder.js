//$(function() {
  
angular.module('routerApp').controller('simpleQueryCtrl', function ($scope, $state, GP, User) {

    $scope.QueryData = function (Query) {
    	Query.terms = Query.terms
    	console.log(Query)
    	Query.queryXml = "<idx:TextSearch><ogc:Literal><idx:Scope>RegistryObject</idx:Scope></ogc:Literal><ogc:Literal><idx:Terms >"
    		+Query.terms+"</idx:Terms></ogc:Literal></idx:TextSearch>";
    	
    	GP.createSimpleQuery(Query).then(
    	       function (content) {
    	    	   if(content.data.success){
    	    		   Query = {}
    	            console.log(content.data)
    	            $scope.ResultData = content.data.data.searchResults;
    	    		   $scope.ResultCount = content.data.data.Result;
    	    	   }
    	       }, function (err) {
    	                // errorCallback
        });
        
    }
    
    $scope.advanceQuery = function(){
    	var Query = {};
		var xml = QueryBuilderView.generateXMLOutput();
		console.log(xml);
		Query.queryXml = xml;
		
		GP.createAdvanceQuery(Query).then(
	    	       function (content) {
	    	    	   if(content.data.success){
	    	    		   Query = {}
	    	            console.log(content.data)
	    	            $scope.AdvanceResultData = content.data.data.searchResults;
	    	    	   }
	    	       }, function (err) {
	    	                // errorCallback
	        });
	}	
	
var queryBuilder = {
    filterOptions: [
        "Contains",
        "Classified As",
        "Classified Under",
        "Inside"
    ],
    logicalOperators: {
        "AND": OpenLayers.Filter.Logical.AND,
        "OR": OpenLayers.Filter.Logical.OR,
        "NOT": OpenLayers.Filter.Logical.NOT
    }
};

var QueryBuilderView = jQuery.fn.extend({
    
    /** Validates all inputs, create query, construct request, display results in table */
    submitQuery: function(evt) {
        var view = this;
        var valid = this.isValid();
        if (valid) {
            $.when(this.createQuery()).done(function(data) {
                // No search criteria given
                if (!data) {
                    new TransactionResponseView({
                        title: "Cannot Query",
                        message: "Unable to query, no filter predicates specified.",
                        success: false
                    });
                    return;
                }
                // Always use distinct (due to id search using an or and spatial properties in possibly more than one Slot)
                var distinct = true;
                if ($("#query-builder-container").length) {
                    $("#hidden-results").empty();
                    $("#query-builder-container").detach().appendTo($("#hidden-results"));
                } else {
                    var contains = $("#query-builder input[name='contains']").val();
                    var classified = $("#query-builder input[name='classified']").val();
                    var classifiedId = $("#query-builder input[name='classified']").attr("data-id");
                    var classifiedOption = $("#query-builder input[name='classified']").closest(".panel").find("select.filter-options :selected").val();
                    var inside = $("#query-builder input[name='inside']").val();
                    var insideShape = $("#query-builder input[name='inside']").data("shape");
                    var id = $("#query-builder input[name='id']").val();

                    var search = {};
                    if (contains != "") {
                        search.contains = contains;
                    }
                    if (classified != "") {
                        search.classified = {};
                        search.classified.value = classified;
                        search.classified.id = classifiedId;
                        search.classified.option = classifiedOption;
                    }
                    if (inside != "") {
                        search.inside = {};
                        search.inside.value = inside;
                        search.inside.shape = insideShape;
                    }
                    if (id != "") {
                        search.id = id;
                    }
                    application.queryBuilderSearch = search;
                }
                var body = view.createGetRecordsRequest(data, distinct, "rim:ExternalIdentifier=left-join");
                $.when(getContent("results.html")).done(function() {
                    new ResultsTableView({
                        el: $("#table-view"),
                        query: getRecords,
                        params: {
                            body: body
                        }
                    });
                });
            });
        }
    },

    createQuery: function() {
        var def = new $.Deferred();
        var view = $(this);
        $.when(cache("object-types", getDomain({
            propertyName: "@objectType",
            include: "all",
            outputFormat: "application/json"
        })), cache("status-types", getDomain({
            propertyName: "@status",
            include: "all",
            outputFormat: "application/json"
        }))).done(function(objectTypesResp, statusTypesResp) {
            // Object types
            var objectTypes = [];
            objectTypesResp[0].domainValues.listOfValues.forEach( function(type, index) {
                objectTypes.push(type.id);
            });
            $.objectTypes = objectTypes;

            // Status types
            var statusTypes = [];
            statusTypesResp[0].domainValues.listOfValues.forEach(function(type, index) {
                statusTypes.push(type.id);
            });
            $.statusTypes = statusTypes;

            var filters = view.parseFilters($(".query-builder-body").children(), []);
            var text = view.formatFilter(filters);
            def.resolve(text);
        });
        return def.promise();
    },

    /** Create a new ogc:Filter instance using OpenLayers.Format.Filter, and returns a string representation of the filter */
    formatFilter: function(filters) {
        var text;
        if (filters) {
            var format = new OpenLayers.Format.Filter({ version: "1.1.0" });
            var node = format.write(filters);
            text = OpenLayers.Format.XML.prototype.write.apply(format, [node]);
            // Change ogc:FeatureId to wrs:RecordId (INdicio does not support it)
            if (text.indexOf("FeatureId") != -1) {
                text = text.replace(/<ogc:FeatureId\s+fid=['"](.*?)['"]\s*\/>/g, "<wrs:RecordId>$1</wrs:RecordId>");
            }
        }
        return text;
    },

    /** Recursively loops through all filters in the query builder, and returns an array of OpenLayers.Filter filters */
    parseFilters: function(container, filters) {
//        var view = $(this);
        $(container).children(".filters").children(".form-inline").each( 
        	function(i, f) {
//        	alert(i);
            if ($(f).hasClass("group")) {
                var groupFilter = $(this).parseFilters($(f).children(), []);
                if (groupFilter) {
                    filters.push(groupFilter);
                }
            } else {
                var filterOption = $(f).find("select.filter-options").val() || $(f).find(".filter-option").text();
                var filterValue = $(f).find("input.value").val();
                if (filterValue != "" && filterValue != undefined) {
                    var filter = $(this).getFilter(filterOption, filterValue, f);
                    if (filter) {
                        filters.push(filter);
                    }
                }
            }
        });
        var operator = $(container).find(".operator :selected").val();
        var filter;
        if (filters.length > 1) {
            // get operator
            filter = new OpenLayers.Filter.Logical({
                type: queryBuilder.logicalOperators[operator],
                filters: filters
            });
        } else {
            filter = filters[0];
        }
        return filter;
    },

    /** Returns an OpenLayers.Filter (based on the selected filter type) */
    getFilter: function(filter, value, parent) {
//        var view = this;
        var filter;
        switch (filter) {
            case "Contains":
                filter = new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.TEXT_SEARCH,
                    scope: "RegistryObject",
                    terms: value
                });
                break;
            case "Classified As":
            case "Classified Under":
                var classifiedOption = filter;
                // Override the text value with the data id, if specified
                value = $(parent).find(".value").attr("data-id") || value;
                if (classifiedOption == "Classified As") {
                    // Special cases for specific schemes (typically not used to classify)
                    // objectType
                    if ($.objectTypes.indexOf(value) > -1) {
                        filter = new OpenLayers.Filter.Comparison({
                            type: OpenLayers.Filter.Comparison.EQUAL_TO,
                            property: "@objectType",
                            value: value
                        });
                    }
                    // statusType
                    else if ($.statusTypes.indexOf(value) > -1) {
                        filter = new OpenLayers.Filter.Comparison({
                            type: OpenLayers.Filter.Comparison.EQUAL_TO,
                            property: "@status",
                            value: value
                        });
                    }
                    // classified by
                    else {
                        var functionFilter = new OpenLayers.Filter.Function({
                            name: "indicio:ClassifiedBy",
                            params: [ value ],
                            properties: ["rim:RegistryObject/@id"]
                        });
                        filter = new OpenLayers.Filter.Comparison({
                            type: OpenLayers.Filter.Comparison.IS_TRUE,
                            value: functionFilter
                        });
                    }
                }
                // classified under
                else if (classifiedOption == "Classified Under") {
                    var functionFilter = new OpenLayers.Filter.Function({
                        name: "indicio:ClassifiedUnder",
                        params: [ value ],
                        properties: ["rim:RegistryObject/@id"]
                    });
                    filter = new OpenLayers.Filter.Comparison({
                        type: OpenLayers.Filter.Comparison.IS_TRUE,
                        value: functionFilter
                    });
                }
                break;
            case "Inside":
                var shape = $(parent).find(".value").data("shape") || "rectangle";
                if (shape == "rectangle") {
                    value = value.split(",");
                    var lowerLat = value[0];
                    var lowerLng = value[1];
                    var upperLat = value[2];
                    var upperLng = value[3];
                    filter = new OpenLayers.Filter.Spatial({
                        type: OpenLayers.Filter.Spatial.BBOX,
                        property: "rim:RegistryObject/rim:Slot/wrs:ValueList/wrs:AnyValue",
                        value: new OpenLayers.Bounds(lowerLat, lowerLng, upperLat, upperLng),
                        projection: "urn:ogc:def:crs:EPSG::4326"
                    });
                }
                else if (shape == "marker") {
                    value = value.split(" ");
                    filter = new OpenLayers.Filter.Spatial({
                        type: OpenLayers.Filter.Spatial.INTERSECTS,
                        property: "rim:RegistryObject/rim:Slot/wrs:ValueList/wrs:AnyValue",
                        value: new OpenLayers.Geometry.Point(value[0], value[1])
                    });
                }
                else if (shape == "polygon") {
                    var coords = value.split(" ");
                    var points = [];
                    $.each(value, function(val, index) {
                        var lat = coords[0];
                        var lng = coords[1];
                        if (lat && lng) {
                            var point = new OpenLayers.Geometry.Point(lat, lng);
                            points.push(point);
                        }
                        coords.splice(0,2);
                    });
                    var linearRing = new OpenLayers.Geometry.LinearRing(points);
                    value = new OpenLayers.Geometry.Polygon([linearRing]);
                    filter = new OpenLayers.Filter.Spatial({
                        type: OpenLayers.Filter.Spatial.INTERSECTS,
                        property: "rim:RegistryObject/rim:Slot/wrs:ValueList/wrs:AnyValue",
                        value: value
                    });
                }
                break;
            case "Id":
                // Cannot put a wrs:RecordId in an ogc:Or (schema-invalid)
                // Creates an ogc:FeatureId (<ogc:FeatureId fid="value"/>), although must change to wrs:RecordId later since INdicio does not support FeatureId
                /*idFilter = new OpenLayers.Filter.FeatureId({
                    fids: [value]
                });*/
                idFilter = new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.EQUAL_TO,
                    property: "@id",
                    value: value
                });
                eiIdFilter = new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.EQUAL_TO,
                    property: "rim:ExternalIdentifier/@value",
                    value: value
                });
                filter = new OpenLayers.Filter.Logical({
                    type: queryBuilder.logicalOperators["OR"],
                    filters: [idFilter, eiIdFilter]
                });
                break;
            default:
                break;
        }
        return filter;
    },

    /** Returns true if all filter inputs are valid (ie. non-empty). If false, adds the Bootstrap
        "has-error" class to the input and parent, and adds an error tooltip. The error class is
        removed when the input changes to a non-empty value */
    isValid: function(addErrorClass) {
        addErrorClass = addErrorClass == false ? false : true;

        var valid = true;
        if ($("#query-builder").hasClass("query-builder-full")) {
            var values = $(this).find(".input-container .value");
            $.each(values, function(i, value) {
                if ($(this).val() == "") {
                    valid = false;
                    if (addErrorClass) {
                        $(this).addClass("has-error");
                        $(this).parent().addClass("has-error");
                        $(this).attr("title", "Provide a valid value").tooltip({
                            placement: "right"
                        });
                    }
                }
            });
        }
        return valid;
    },

    /** Validates all inputs, create query, then displays the "save query" modal */
    saveQuery: function(evt) {
        var view = this;
        var valid = this.isValid();
        if (valid) {
            $.when(this.createQuery()).done(function(data) {
                if (typeof saveQueryView !== "undefined") {
                    saveQueryView.undelegateEvents();
                    //saveQueryView.$el.empty();
                    //saveQueryView.stopListening();
                }
                saveQueryView = new SaveQueryView({
                    query: data,
                    el: view.$el
                });
            });
        }
    },

    /** Returns a GetRecords request */
    createGetRecordsRequest: function(query, distinct, joins) {
        var templateStr = "<GetRecords xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" "
            +   "xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" "
            +   "xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" "
            +   "xmlns:wrs=\"http://www.opengis.net/cat/wrs/1.0\" "
            +   "xmlns:gml=\"http://www.opengis.net/gml/3.2\" "
            +   "xmlns:ogc=\"http://www.opengis.net/ogc\" "
            +   "xmlns:idx=\"urn:x-galdosinc:indicio:web-registry-service\" "
            +   "service=\"CSW\" version=\"2.0.2\" "
            +   "startPosition=\"1\" maxRecords=\"10\" "
            +   "resultType=\"results\" outputFormat=\"application/json; charset=UTF-8\">"
            +   "<Query typeNames=\"rim:RegistryObject\">";
        if (typeof distinct !== "undefined" && distinct) {
            templateStr += "<?indicio-distinct-values true?>";
        }
        if (typeof joins !== "undefined") {
            templateStr += "<?indicio-filter-joins " + joins + "?>";
        }
        templateStr += "<ElementSetName typeNames=\"rim:RegistryObject\">full</ElementSetName>"
            +       "<Constraint version=\"1.1.0\"> <%= filter %> </Constraint>"
            +   "</Query>"
            +   "</GetRecords>";
        var template = $.template(templateStr);
        var getRecords = template({
            filter: query
        });
        return getRecords;
    },

    /** Validates all inputs, create query, then displays the XML in a CodeMirror text editor */
    generateXMLOutput: function(evt) {
        var view = $(this);
        var valid = $(this).isValid();
        var xml = '';
        if (valid) {
            $.when($(this).createQuery()).done(function(data) {
                if (data) {
                    console.log(data);
                    xml= data;
                } else {
                    $("#xml-output .well").remove();
                    $("#xml-output").append("<div class='well'>Invalid query provided. Please return to the Query Builder tab.</div>");
                    $("#xml-output textarea").hide();
                }
            });
        } else {
            $("#xml-output .well").remove();
            $("#xml-output").append("<div class='well'>Invalid query provided. Please return to the Query Builder tab.</div>");
            $("#xml-output textarea").hide();
        }
        return xml;
    },

    
    /** Takes an existing OGC filter and re-constructs the query builder */
    rebuildQueryBuilder: function(xml) {
        xml = typeof xml == "string" ? $.parseXML(xml) : xml;

        var operator = this.getOperator(xml);
        $.each($(operator.element).children(), function(child, index) {
            if (child.localName == "And" || child.localName == "Or") {
                if ($(child).parent().get(0).localName !== "Filter") {
                    $(".add-group").last().trigger("click");
                }
                $(".operator").last().find("option[value='" + child.localName.toUpperCase() + "']").prop("selected", true).trigger("change");
                this.rebuildQueryBuilder(child);
            } else {
                var filterType = child.localName;
                switch (filterType) {
                    case "TextSearch":
                        $(".add-condition").last().trigger("click");
                        var filter = $(".filters .filter").last();
                        var literal = $(child).find("Literal,ogc\\:Literal").last();
                        literal = literal.find("Terms,idx\\:Terms").text();
                        filter.find(".value").val(literal);
                        break;
                    case "PropertyIsTrue":
                        $(".add-condition").last().trigger("click");
                        var classified = $(child).find("Function,ogc\\:Function").attr("name");
                        classified = classified.split(":")[1];

                        var filter = $(".filters .filter").last();
                        var select = filter.find(".filter-options");
                        if (classified == "ClassifiedBy") {
                            select.find("option[value='Classified As']").prop("selected", true);
                        } else {
                            select.find("option[value='Classified Under']").prop("selected", true);
                        }
                        select.trigger("change");

                        var literal = $(child).find("Literal,ogc\\:Literal").text();
                        filter.find(".value").val(getEnding(literal)).attr("data-id", literal);
                        break;
                    case "BBOX":
                    case "Intersects":
                        $(".add-condition").last().trigger("click");
                        var filter = $(".filters .filter").last();
                        var select = filter.find(".filter-options option[value='Inside']");
                        select.prop("selected", true);
                        select.trigger("change");

                        var coordinates;
                        if (filterType == "BBOX") {
                            var lowerCorner = $(child).find("lowerCorner,gml\\:lowerCorner").text();
                            var upperCorner = $(child).find("upperCorner,gml\\:upperCorner").text();
                            coordinates = lowerCorner + " " + upperCorner;
                        } else if (filterType == "Intersects") {
                            var shape = $(child).children("Point,gml\\:Point");
                            if (shape.length) {
                                coordinates = shape.find("pos,gml\\:pos").text();
                            } else {
                                coordinates = "[ Too large to display ]";
                            }
                        }
                        filter.find(".value").val(coordinates);
                        break;
                    default:
                        break;
                }
            }
        }, this);
    },

    getOperator: function(xml) {
        var operator = {};
        if (xml.localName == "And" || xml.localName == "Or") {
            operator.name = xml.localName;
            operator.element = xml;
        } else {
            var children = $(xml).find("Filter,ogc\\:Filter").get(0);
            operator.name = children.localName;
            operator.element = children;
        }
        return operator;
    }
});

//$("#query-builder").on("click", ".query",QueryBuilderView.generateXMLOutput);

    
var OperatorView = jQuery.fn.extend({
	

    /** Updates the operator name (between filters) when the operator select menu changes */
    changeOperator: function(evt) {
        var operator = $(evt.currentTarget).val().toLowerCase();
        var filters = $(this).closest(".filter-container").children(".filters");
        var groupOperators = filters.children(".operator-display");
        filters.children(".filter").children(".operator-display").text(operator);
        groupOperators.text(operator);
        filters.children(".filter").first().children(".operator-display").text("");
    }
});

$("#query-builder").on("change", ".operator",OperatorView.changeOperator);


var ButtonView = jQuery.fn.extend({
	
	
        
    /** Adds a filter condition to the current parent */
    addCondition: function(evt) {
    	var newCondition = $('<div class="form-inline filter"></div>');
    	var logicalOperator = $(this).closest(".filter-container").find(".operator :selected").val();
    	newCondition.loadTemplate($("#conditionContainer"), {operator: logicalOperator}).appendTo(
        $(this).closest(".filter-container").children(".filters"));
    },

    /** Adds a group filter condition to the current parent */
    addGroup: function(evt) {
    	var bg = $(evt.currentTarget).closest(".group").hasClass("well") ? "white-bg" : "well";
    	var newGroup = $('<div/>');
    	var logicalOperator = $(this).closest(".filter-container").find(".operator :selected").val();
    	newGroup.loadTemplate($("#groupContainer"), {operator: logicalOperator});
    	if($(evt.currentTarget).closest(".group").hasClass("white-bg")){
    		newGroup.find(".white-bg").addClass("well");
    		newGroup.find(".white-bg").removeClass("white-bg");
    	}
        $(this).closest(".filter-container").children(".filters").append(newGroup.html());
    },

    /** Clear all filter conditions from a parent (or all filters if currently a top-level filter) */
    clearAll: function(evt) {
        if ($(evt.currentTarget).hasClass("top-level")) {
            $(".query-builder-body").find(".filters").empty();
        } else {
            var parent = $(evt.currentTarget).closest(".filter");
            var group = $(evt.currentTarget).closest(".group");
            group.prev(".operator-display").remove();
            parent.remove();

        }
    }
    
});

$("#query-builder").on("click", ".add-condition",ButtonView.addCondition);
$("#query-builder").on("click",".add-group", ButtonView.addGroup);
$("#query-builder").on("click",".clear-all", ButtonView.clearAll);


var FilterView = jQuery.fn.extend({
   
    /** Removes a filter from the query builder */
    removeFilter: function(evt) {
        var parent = $(evt.currentTarget).closest(".filter");
        var container = $(evt.currentTarget).closest(".filters");
        parent.remove();
        this.remove();
        container.children(".filter").first().children(".operator-display").text("");
    },

    /** Displays the available inputs for the currently selected filter option */
    displayFilterInputs: function(evt) {
        var option = $(evt.currentTarget).val();
        var template;
        switch (option) {
            case "Contains":
                template = "<input type='text' class='value form-control input-sm' name='contains'>";
                break;
            case "Classified As":
            case "Classified Under":
                template = "<input type='text' class='value tree-input form-control input-sm' name='classified'><div class='tree-container'></div>";
                break;
            case "Inside":
                template = "<span class='input-group'>"
                    +       "<span class='input-group-btn'>"
                    +           "<button class='open-map btn btn-default btn-sm' data-toggle='modal' data-target='#map-modal'>Edit in Map</button>"
                    +       "</span>"
                    +       "<input type='text' class='value inside form-control input-sm' name='inside'>"
                    +   "</span>";
                break;
        }
        $(evt.currentTarget).parent(".filter").find(".input-container").empty().html(template);
    }
    });
$("#query-builder").on("click", ".remove-filter",FilterView.removeFilter);
$("#query-builder").on("change",".filter-options", FilterView.displayFilterInputs);

$("#container").on("click", ".tree-input", function() {
    TreeWidgetView.initialize({
//        el: $(this).parent().find(".tree-container"),
        searchElement: $(".tree-input"),
        containerHeight: "300px",
        closeOnClickout: true
    });
});

$(".tree-input").on("keyup",TreeWidgetView.search);
//
//$(".tree").on("click",".node", TreeWidgetView.clickNode)
//$(".tree").on("dblclick", TreeWidgetView.doubleClickNode);


$('#map-modal').on('shown.bs.modal', function (e) {
	
//	var source = new ol.source.Vector({wrapX: false});
	
		var map = new ol.Map({
		  layers: [
		    new ol.layer.Tile({
		      source: new ol.source.OSM()
		    })
		  ],
		  target: 'bbox',
		  view: new ol.View({
			  projection : 'EPSG:4326',
				center : [ 77.695313, 22.917923 ],
				zoom : 4
		  })
		});

		// a normal select interaction to handle click
//		var select = new ol.interaction.Select();
//		map.addInteraction(select);
//
//		var selectedFeatures = select.getFeatures();

		
		
		// a DragBox interaction used to select features by drawing boxes
		var drag = function(){return true;}
//		var dragBox = new ol.interaction.DragBox({
//		  condition: drag
//		});
//
//		map.addInteraction(dragBox);

//		map.addInteraction(new ol.interaction.Draw({
//            source: source,
//            type: ('Circle'),
//            condition: drag,
//            geometryFunction: ol.interaction.Draw.createRegularPolygon(4)
//          }));
		
//		dragBox.on('boxend', function() {
//		  // features that intersect the box are added to the collection of
//		  // selected features
//		 dragBox.getGeometry().getExtent();
//		  bbox = dragBox.getGeometry().getExtent();
//		  
//		});
		
		extent = new ol.interaction.Extent({
	        condition: drag
	      });
	      map.addInteraction(extent);
	      extent.setActive(true);
		
		// clear selection when drawing a new box and when clicking on the map
//		dragBox.on('boxstart', function() {
////		  selectedFeatures.clear();
//		});

	})
	
	$('#map-modal').on('hide.bs.modal', function (e) {
		console.log("inside");
		$("#bbox").html("");
		
	});

	$('#map-modal').on('click',"button[name='save-coords']", function (e) {
		
		console.log(extent.getExtent());
		$(".inside ").val(extent.getExtent());
		
	});

});