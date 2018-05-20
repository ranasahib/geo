var nodeId = 0;

//Assumes prefetchSchemes is run before this promise is used elsewhere
var getClassificationSchemesPromise;

/** Preload scheme data */
function prefetchSchemes(refresh) {
    // When working with a pre-defined promise, must re-init
    if (typeof cache("classification-schemes") === "undefined" || (typeof refresh !== "undefined" && refresh)) {
        removeCacheKey("classification-schemes");
        getClassificationSchemesPromise = storedQuery({
            qid: "urn:x-indicio:def:stored-query:all-objects-of-type",
            type: "urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:ClassificationScheme",
            maxRecords: "1000",
            include: "all",
            ssk: "code",
            elementSetName: "full",
            outputFormat: "application/json"
        },
        {
            beforeSend: function(jqXHR) {
                setAcceptLanguage(jqXHR, "*");
            }
        })
    }
    cache("classification-schemes", getClassificationSchemesPromise);
}	

prefetchSchemes();

function sortByName(a, b) {
    var idA = a.name && getObjectByLang(a.name) ? getObjectByLang(a.name).value : a.id;
    var idB = b.name && getObjectByLang(b.name) ? getObjectByLang(b.name).value : b.id;
    return (idA < idB ? -1 : (idA > idB ? 1 : 0));
}

var TreeWidgetView = jQuery.fn.extend({
//    template: "<div class='tree scrollable'></div>",
    initialize: function(options) {
        // Options
        this.searchElement = options.searchElement;
        this.containerHeight = options.containerHeight;
        this.closeOnClickout = options.closeOnClickout;

        // ------- Constants -------

        // Size
        this.margin = { top: 20, right: 20, bottom: 0, left: 20 };
        this.width = 300;
        //this.height = options.height;

        // Node
        this.rootNode = "ClassificationSchemes";
        this.nodeRadius = 5;
        this.nodeHeight = 20;
        this.leafNodeColour = "#f0f0f0"; // white
        this.oddNodeColour = "#b0c4de"; // blue
        this.evenNodeColour = "#ffcc33"; // gold

        // Tree
        this.tree = d3.layout.tree().nodeSize([0,20]);

        // Paths
        this.diagonal = d3.svg.line().interpolate("step")
            .x(function(d) {
                return d.x;
            })
            .y(function(d) {
                return d.y;
            });

        // Misc
        this.duration = 400;
        this.selectedNode = null;
        this.lastKeyCode = null;

        this.render();
    },
    render: function() {
        var view = this;

        $(".tree-container").html("<div class='tree scrollable'></div>");

        // Float the tree container
        if (this.containerHeight) {
        	$(".tree-container").find(".tree").css({
                "z-index": 1000,
                "height": "300px",
                "overflow-y": "scroll",
                "overflow-x": "hidden",
                "position": "absolute",
                "border": "2px solid #ddd",
                "margin-top": "8px",
                "background": "white"
            });
        }

        // Fetch CSchemes
        $.when(this.getClassificationSchemes()).done(function(data) {
            var list = {
                "parent": "null",
                "name": view.rootNode
            };
            // Sort the root schemes by name or id
            var results = data.searchResults;
            results.sort(sortByName);
            list.children = view.parseCNodes(results, view.rootNode, []);
            view.root = list;
            view.old = list;
            view.selectElements();
            view.collapseNodes(view.root);
            view.drawTree();
            view.clickNode(view.root);
        });

        // Search on text
        $(this.searchElement).on("keyup", function(event) {
            view.search(event);
        });
        
        // Close when focus is lost
        $(document).on("click", function(event) {
            if (!$(event.target).closest(".tree").hasClass("tree") && !$(event.target).hasClass("tree-input")) {
                view.closeTree();
            }
        });
    },

    /** Returns a promise to the ClassificationScheme response */
    getClassificationSchemes: function() {
        return cache("classification-schemes", getClassificationSchemesPromise);
    },

    /** Returns a JSON formatted tree:
            tree = {
                "name": -- node name
                "id": -- node id
                "parent": -- parent node
                "children": -- node's cnodes (array)
                "old": -- node's cnodes (untouched)
                "record": -- node data
            }
    */
    parseCNodes: function(data, parent, list) {
        data.forEach( function(node, index) {
            var name = getObjectByLang(node.name).value || node.code;
            var children = [];
            if (node.classificationNodes) {
                children = this.parseCNodes(node.classificationNodes, name, children);
            }
            list.push({
                "name": name,
                "id": node.id,
                "parent": parent,
                "children": children.length > 0 ? children : null,
                "old": children.length > 0 ? children : null,
                "record": node
            })
        },this);
        return list;
    },

    /** Select the current SVG element */
    selectElements: function() {
        var view = this;
        $(".tree-container").find(".tree").empty();
        this.svg = d3.select($(".tree-container").find(".tree").get(0)).append("svg")
            .attr("width", this.width)
            .append("g")
            .attr("transform", "translate(" + this.margin.left + "," + this.margin.top + ")");
    },

    /** Draw the tree */
    drawTree: function() {
        var view = this;

        this.nodes = this.tree.nodes(this.root);
        this.height = this.nodes.length * this.nodeHeight + this.margin.top + this.margin.bottom;

        d3.select("svg").attr("height", this.height);
        d3.select(self.frameElement).style("height", this.height + "px");

        this.nodes.forEach(function(n, i) {
            n.x = i * view.nodeHeight;
        });

        var node = this.svg.selectAll("g.node")
            .data(this.nodes, function(d) {
                return d.id || (d.id = ++nodeId);
            });

        var nodeEnter = node.enter().append("g")
            .attr("id", function(d) {
                return d.id;
            })
            .attr("class", "node")
            .style("opacity", 1e-6);

        nodeEnter.append("circle")
            .attr("r", this.nodeRadius)
            .style("fill", function(d) {
                return view.getNodeColour(d);
            })
            .on("click", function(d) {
                view.clickNode(d);
            })
            .on("dblclick", function(d) {
                view.doubleClickNode(d);
            });

        nodeEnter.append("text")
            .attr("class", "node-text")
            .attr("dy", 3.5)
            .attr("dx", 10.5)
            .on("click", function(d) {
                view.clickNode(d);
            })
            .on("dblclick", function(d) {
                view.doubleClickNode(d);
            })
            .text(function(d) {
                return d.name.length > 40 ? d.name.substring(0,40) + "..." : d.name;
            });

        nodeEnter.append("svg:title")
            .text(function(d) {
                if (d.id) {
                    return d.id;
                }
            });

        nodeEnter.attr("transform", function(d) {
                var y = d.y || 0;
                var x = d.x || 0;
                return "translate(" + d.y + "," + d.x + ")";
            })
            .style("opacity", 1);

        node.attr("transform", function(d) {
                var y = d.y || 0;
                var x = d.x || 0;
                return "translate(" + d.y + "," + d.x + ")";
            })
            .style("opacity", 1)
            .select("circle")
            .style("fill", function(d) {
                return view.getNodeColour(d);
            });

        node.exit()
            .style("opacity", 1e-6)
            .remove();

        var link = this.svg.selectAll("path.link")
            .data(this.tree.links(this.nodes), function(d) {
                return d.target.id;
            });

        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("target", function(d) {
                return d.target.name;
            })
            .attr("d", function(d) {
                var num = d.source.x <= d.source.y ? d.source.x : d.source.y;
                return "M" + d.source.y +
                       "," + d.source.x +
                       "H" + num +
                       "V" + d.target.x +
                       "H" + d.target.y;
            });

        link.attr("d", function(d) {
                var num = d.source.x <= d.source.y ? d.source.x : d.source.y;
                return "M" + d.source.y +
                       "," + d.source.x +
                       "H" + num +
                       "V" + d.target.x +
                       "H" + d.target.y;
            });

        link.exit().remove();

        // Stash the old positions for transition
        $.each(this.nodes, function(i, node) {
            node.x0 = node.x;
            node.y0 = node.y;
        });

    },

    /** Returns a node colour depending on the current depth of the node */
    getNodeColour: function(d) {
        if (d.children == null && d._children == null) {
            return this.leafNodeColour;
        } else if (this.isOdd(d.depth)) {
            return this.oddNodeColour;
        } else {
            return this.evenNodeColour;
        }
    },

    /** Returns true if the current depth is at an odd number */
    isOdd: function(num) {
        return (num % 2) == 1;
    },

    /** Expands or collapses the children of the selected node */
    clickNode: function(d) {
        this.updateSelectedOption(d);
        this.lastKeyCode = null;

        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }

        this.drawTree();
    },

    /** Set the input value to the selected node. Highlight the selected node. */
    updateSelectedOption: function(d) {
        this.selectedNode = d;

        if (d.name != this.rootNode) {
            // Trigger change so that other views can act accordingly
            $(this.searchElement)
                .val(d.name)
                .attr("data-id", d.id)
                .data("record", d.record).change();
        }

        $(".tree-container").find("g").find("text.selected").addClass("unselected");
        $(".tree-container").find("g[id='" + d.id + "']").find("text").addClass("selected");
    },

    /** Collapses all nodes (but does *not* redraw the tree; must call this.drawTree() after this.collapseNodes() to see changes) */
    collapseNodes: function(d) {
        var view = this;
        if (d.children) {
            $.each(d.children, function(i, node) {
                view.collapseNodes(node);
            });
            d._children = d.children;
            d.children = null;
        }
    },

    /** Expands all nodes (but does *not* redraw the tree; must call this.drawTree() after this.expandNodes() to see changes) */
    expandNodes: function(d) {
        var view = this;
        if (d.children) {
            $.each(d.children, function(i, node) {
                view.expandNodes(node);
            });
            d.children = d.children;
        } else if (d._children) {
            $.each(d._children, function(i, node) {
                view.expandNodes(node);
            });
            d.children = d._children;
            d._children = null;
        }
    },

    /** Returns nodes whose name/code matches part of the provided text */
    search: function(event) {
    	var text = $(event.currentTarget).val();
    	var keyCode = event.keyCode;
        var view = this;
        var code = keyCode || 65;

        // Prevent searches if the arrow keys are pressed
        if (code == 37 || code == 38 || code == 39 || code == 40) {
            //keydown(event);
            return;
        }
        // Reset the tree if the search field is empty
        else if (text == "") {
            $(this.searchElement).val("");
            this.resetTree();
        }
        // Handle all other key presses
        else {
            this.expandNodes(this.root);

            // Undo the filter if the backspace key is pressed
            if (code == 8) {
                this.tree.nodes(this.root).filter(function(d, i) {
                    d.children = d.old;
                });
            }

            // Return only matching nodes
            var matches = [], names = [];
            this.tree.nodes(this.root).filter(function(d, i) {
                var name = d.name.toLowerCase();
                if (name.indexOf(text) > -1) {
                    names.push(d.name);
                    matches.push(d);
                }
            });

            if (matches.length) {
                // For each matching node, traverse up the tree to find all parents (up to the root node)
                $.each(matches, function() {
                    var returnVal = view.getParents(this);
                    if (returnVal.length > 0) {
                        $.each(returnVal, function() {
                            // Prevent duplicate parents
                            if (matches.indexOf(this) == -1)  {
                                matches.push(this);
                                names.push(this.name);
                            }
                        });
                    }
                });

                // Only display the nodes that matched
                this.tree.nodes(this.root).filter(function(d) {
                    if (d.children) {
                        if (names.indexOf(d.name) > -1) {
                            var matchingChildren  = [];
                            $.each(d.children, function(i, node) {
                                if (names.indexOf(node.name) > -1) {
                                    matchingChildren.push(node);
                                }
                            });
                            d.children = null;
                            d._children = null;
                            if (matchingChildren.length > 0) {
                                d.children = matchingChildren;
                            }
                        }
                    }
                });
            } else {
                this.root.children = [{
                    "name": "No match",
                    "id": "No match"
                }]
            }

            // Remove existing paths
            $("path").remove();

            // Update the tree with all the non-matching nodes removed
            // and the matched node expanded
            this.expandNodes(this.root);
            this.drawTree();
        }
    },

    /** Returns a list of all parents (up to the top level node) */
    getParents: function(node, list) {
        list = list || [];
        if (node.parent) {
            list.push(node.parent);
            if (node.parent.parent) {
                this.getParents(node.parent, list);
            }
        }
        return list;
    },

    /** Resets the tree to its original state */
    resetTree: function() {
        this.render();
    },

    /** Closes the tree widget when clicking out anywhere */
    closeTree: function() {
        this.closeOnClickout = this.closeOnClickout == false ? false : true;
        if (this.closeOnClickout) {
        	$(".tree-container").find(".tree").hide();
        }
    }
});