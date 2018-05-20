var routerApp = angular.module('routerApp', ['shagstrom.angular-split-pane','ui.router','ui-notification']);

routerApp.run(['$anchorScroll', function($anchorScroll) {
	  $anchorScroll.yOffset = 0;   // always scroll by 50 extra pixels
	}])

routerApp.config(function ($stateProvider, $urlRouterProvider, $httpProvider) {

    $urlRouterProvider.otherwise('/login');

    $stateProvider

        // HOME STATES AND NESTED VIEWS ========================================
        .state('login', {
            url: '/login',
            templateUrl: 'templates/login.html',
            controller: "LoginCtrl"
        })
        .state('register', {
            url: '/register',
            templateUrl: 'templates/register.html',
            controller:'RegisterCtrl'
        })
        .state('forgotPassword', {
            url: '/forgotPassword',
            templateUrl: 'templates/forgotPassword.html',
            controller:'ForgotPasswordCtrl'
        })        
        .state('recoverPassword', {
            url: '/recoverPassword/{token}',
            templateUrl: 'templates/recoverPassword.html',
            controller:'ForgotPasswordCtrl'
        })
        .state('acceptInvitation', {
            url: '/acceptInvitation/{token}',
            templateUrl: 'templates/invite.html',
            controller:'InviteCtrl'
        })
        .state("app", {
            url: "/app",
            abstract: true,
            templateUrl: 'templates/app.html',
            controller: "AppCtrl"
        })
        .state('app.home', {
            url: '/home',
            views: {
					'appContent': {
						templateUrl: 'templates/home.html'
					}
			}
        })
        .state('app.roles', {
            url: '/roles',
            views: {
					'appContent': {
						templateUrl: 'templates/roles.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.create_role', {
            url: '/create_role',
            views: {
					'appContent': {
						templateUrl: 'templates/role/createRole.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.list_role', {
            url: '/list_role',
            views: {
					'appContent': {
						templateUrl: 'templates/role/listRole.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.role_permission', {
            url: '/role_permission',
            views: {
					'appContent': {
						templateUrl: 'templates/role/rolePermission.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.users', {
            url: '/users',
            views: {
					'appContent': {
						templateUrl: 'templates/users.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.user_list', {
            url: '/user_list',
            views: {
					'appContent': {
						templateUrl: 'templates/user/user_list.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.user_approve', {
            url: '/user_approve',
            views: {
					'appContent': {
						templateUrl: 'templates/user/user_approve.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.user_submit', {
            url: '/user_submit',
            views: {
					'appContent': {
						templateUrl: 'templates/user/user_submit.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.user_create', {
            url: '/user_create',
            views: {
					'appContent': {
						templateUrl: 'templates/createUser.html',
                         controller: "UserCtrl"
					}
			}            
        })
        .state('app.harvest_list', {
            url: '/harvest_list',
            views: {
					'appContent': {
						templateUrl: 'templates/harvest_list.html',
                        controller: "HarvestListCtrl"
					}
			}            
        })
        .state('app.harvest', {
            url: '/harvest',
            views: {
					'appContent': {
						templateUrl: 'templates/harvest.html',
                        controller: "HarvestCtrl"
					}
			}            
        }).state('app.simpleQuery', {
            url: '/simpleQuery',
            views: {
					'appContent': {
						templateUrl: 'templates/query.html',
                        controller: "simpleQueryCtrl"
					}
			}            
        }).state('app.advanceQuery', {
            url: '/advanceQuery',
            views: {
					'appContent': {
						templateUrl: 'templates/advanceQuery.html',
                        controller: "advanceQueryCtrl"
					}
			}            
        })
        .state('query.advanceResult', {
            url: '/advanceResult',
            views: {
				'appContent': {
					templateUrl: 'templates/map.html'
				}
		    } 
        })
        .state('mapView', {
            url: '/mapView?accessUrl',
            templateUrl: 'templates/map.html',
			controller: "MapCtrl"
        })
        .state('mapQuery', {
            url: '/mapQuery/{lat}/{long}/{radius}',
            templateUrl: 'templates/map_query.html',
			controller: "MapQueryCtrl"
        })
        .state('complexQuery', {
            url: '/complexQuery',
            templateUrl: 'templates/complex_query.html',
			controller: "ComplexQueryCtrl"
        })
        .state('app.organisation', {
            url: '/organisation',
            views: {
					'appContent': {
						templateUrl: 'templates/organisation.html',
                        controller: "OrganisationCtrl"
					}
			}            
        })
        .state('app.inactive_organisation', {
            url: '/inactive_organisation',
            views: {
					'appContent': {
						templateUrl: 'templates/unapproved_organisation.html',
                        controller: "InactiveOrganisationCtrl"
					}
			}            
        })
        .state('app.organisationList', {
            url: '/organisationList',
            views: {
					'appContent': {
						templateUrl: 'templates/organisationList.html',
                        controller: "OrganisationCtrl"
					}
			}            
        })
        .state('app.viewRecord', {
            url: '/viewRecord/{id}',
            views: {
					'appContent': {
						templateUrl: 'templates/viewRecord.html',
                        controller: "ViewRecordCtrl"
					}
			}
            
        })
        .state('app.viewUser', {
            url: '/viewUser/{id}',
            views: {
					'appContent': {
						templateUrl: 'templates/user/viewUser.html',
                        controller: "ViewRecordCtrl"
					}
			}
            
        })
        .state('emailVerification', {
            url: '/emailVerification',
            templateUrl: 'templates/emailVerification.html'
        });

    $httpProvider.interceptors.push(['$q', '$location', '$window', '$rootScope', 'User', function ($q, $location, $window, $rootScope, User) {
        return {
            'request': function (config) {
            	var user = User.get();
            	var token = null
            	if(user)
                var token = user.token;
                if (token) {
                    config.headers.Authorization = 'bearer ' + token;
                } else if (!($location.path().startsWith("/mapView")) && $location.path() !== '/complexQuery' && $location.path() !== '/advanceQuery' && $location.path() !== '/simpleQuery' && $location.path() !== '/mapLayer' && $location.path() !== '/register' && $location.path() !== '/login' && $location.path() !== '/emailVerification' && $location.path() !== '/forgotPassword' && !($location.path().startsWith("/acceptInvitation")) && !($location.path().startsWith("/recoverPassword"))) {
                    $location.path('/login');
                }
                return config;
            },
            'response': function (response) {
                $rootScope.showLoader = false;
                return response;
            },
            'responseError': function (response) {
                $rootScope.showLoader = false;
                if (response.status === 401) {
                    $location.path('/login');
                } else if (response.status === 406) {
                    alert("You do not have rights to perform this action. Kindly Contact admin");
                } else if (response.status === 413) {
                    alert('File is too large');
                } else {
                    console.log("Oops som error occured!!")
                }
                return $q.reject(response);
            }
        };
    }]);

});


routerApp.controller('AppCtrl', function ($scope, $state, GP, User) {
    $scope.menus = User.get().menus;
    console.log($scope.menus)
    
    $scope.logout = function(){
    	User.save(null);
    	$state.go("login")
    }
    
    $scope.showModal = false;
    $scope.open = function(){
        $scope.showModal = !$scope.showModal;
        $scope.showModal = !$scope.showModal;
    };
})

routerApp.controller('LoginCtrl', function ($scope, $state, GP, User,Notification) {

	$scope.login = function (user) {
	   Notification.error("Testing")
	}
	
	$scope.login = function (user) {
        GP.login(user).then(
            function (content) {
                var res = content.data;
                if (res.success) {
                    $scope.otpbox = true;
                }else{
                	//$scope.loginmessage = content.data.message
                	Notification.error(content.data.message);

                }
            }, function (err) {
                // errorCallback
            });
    }
	
	
    $scope.otpverify = function (user) {
        GP.verifyOtp(user).then(
            function (content) {
                var res = content.data;
                if (res.success) {
                    User.save(content.data.data);
                    $state.go("app.home")
                }else{
                	//$scope.loginmessage = content.data.message
                	Notification.error(content.data.message);

                }
            }, function (err) {
                // errorCallback
            });
    }
});

routerApp.controller('ForgotPasswordCtrl', function ($scope, $state, GP, User, Notification) {
	$scope.forgotPassword = function(user){
	   GP.forgotPassword(user.loginName).then(
	            function (content) {
	            	if(content.data.success){
		                  Notification.info("Check your inbox");
		                  $state.go("login");
		            	}
		            	else{
			              Notification.error(content.data.message);	            		
		            	}
	      }, function (err) {
	                // errorCallback
	    });
	}
	
	$scope.recoverPassword = function(user){
		GP.recoverPassword($state.params.token,user).then(
	            function (content) {
	            	if(content.data.success){
	                  Notification.info(content.data.message);
	                  $state.go("login");
	            	}
	            	else{
		              Notification.error(content.data.message);	            		
	            	}
	      }, function (err) {
	                // errorCallback
	    });
	}
});

routerApp.controller('RegisterCtrl', function ($scope, $state, GP, User, Notification) {

	$scope.user = {}
	
	$scope.nextPage = function(user){
		user.registryOrganisationId = user.organisation.registryId;
		$scope.isNext = true;
		$scope.user = user;
	}
	$scope.prevPage = function(user){
		$scope.isNext = false;
		$scope.user = user;
	}

    $scope.register = function (user) {
    	console.log(user)
        GP.register(user).then(
            function (content) {
            	if(content.data.success){
	                  Notification.info(content.data.message);
	                  $state.go("login");
	            	}
	            	else{
		              Notification.error(content.data.message);	            		
	            	}
            }, function (err) {
                // errorCallback
            });
    }
    
    $scope.aadhar = "2";
    $scope.pan = "2";
    $scope.validateIdentityProof = function(name,value,length){
       if(value.length == length){ 	
    	 GP.validateIdentityProof(name,value).then(
                function (content) {
                	if(content.data.success){
                		if(name == "AADHAR")
                 		   $scope.aadhar = "1";
                		if(name == "PAN")
                 		   $scope.pan = "1";
    	                  Notification.info(content.data.message);
    	            	}
    	            	else{
                    		if(name == "PAN")
                     		   $scope.pan = "0";
                    		if(name == "AADHAR")
                     		   $scope.aadhar = "0";
    		              Notification.error(content.data.message);	            		
    	            	}
                }, function (err) {
                    // errorCallback
                });
    	}
       else{
    	   if(name == "AADHAR")
     		   $scope.aadhar = "0";
    		if(name == "PAN")
     		   $scope.pan = "0";
       }
    }
    
    $scope.listOrganisations = function () {
       GP.organisationList().then(
               function (content) {
                   $scope.organisations = content.data.data;
               }, function (err) {
                   // errorCallback
               });
    }
    $scope.listOrganisations()
});

routerApp.controller('InviteCtrl', function ($scope, $state, GP, User,Notification) {

	$scope.aadhar = "2";
    $scope.pan = "2";
    $scope.validateIdentityProof = function(name,value,length){
       if(value.length == length){ 	
    	 GP.validateIdentityProof(name,value).then(
                function (content) {
                	if(content.data.success){
                		if(name == "AADHAR")
                 		   $scope.aadhar = "1";
                		if(name == "PAN")
                 		   $scope.pan = "1";
    	                  Notification.info(content.data.message);
    	            	}
    	            	else{
                    		if(name == "PAN")
                     		   $scope.pan = "0";
                    		if(name == "AADHAR")
                     		   $scope.aadhar = "0";
    		              Notification.error(content.data.message);	            		
    	            	}
                }, function (err) {
                    // errorCallback
                });
    	}
       else{
    	   if(name == "AADHAR")
     		   $scope.aadhar = "0";
    		if(name == "PAN")
     		   $scope.pan = "0";
       }
    }
	
	var token = $state.params.token;
    $scope.register = function (user) {
        GP.acceptInvite(token,user).then(
            function(content) {
			  if (content.data.success) {
				var res = content.data;
				$state.go("login")
			 } else {
				Notification.error(content.data.message);
			 }
		}, function (err) {
                // errorCallback
            });
    }
});


routerApp.controller('HarvestCtrl', function ($scope, $state, GP, User,Notification) {

    $scope.harvestData = function (harvest) {
    	harvest.storeNames = [harvest.storeName]
    	harvest.nodeNames = [harvest.nodeName]
    	
    	harvest.dataAccess = harvest.dataAccessObj.id;
    	harvest.dataClassification = harvest.dataClassificationObj.id;
    	harvest.price = harvest.priceObj.id;
    	
    	GP.createHarvest(harvest).then(
    	       function (content) {
    	    	   if(content.data.success){
    	    		   $scope.harvest = {}
    	    		   Notification.info("Harvest Successfull");
    	    		   $state.go("app.harvest_list")
    	    	   }
    	    	   else{
    	    		   Notification.error("Some error occured");	    
    	    	   }
    	       }, function (err) {
    	                // errorCallback
        });
    }
    
    GP.listClassification("DataAccess").then(
 	       function (content) {
	    	   if(content.data.success){
	    		   $scope.dataAccesses = content.data.data;
	    	   }
	       }, function (err) {
	                // errorCallback
    });
    
    GP.listClassification("DataClassification").then(
 	       function (content) {
	    	   if(content.data.success){
	    		   $scope.dataClassifications = content.data.data;
	    	   }
	       }, function (err) {
	                // errorCallback
    });
    
    GP.listClassification("Price").then(
 	       function (content) {
	    	   if(content.data.success){
	    		   $scope.prices = content.data.data;
	    	   }
	       }, function (err) {
	                // errorCallback
    });
    
    GP.listUser().then(function (content) {
    	$scope.users = content.data.data;
    }, function (err) {
	                // errorCallback
    });
});

//routerApp.controller('simpleQueryCtrl', function ($scope, $state, GP, User) {
//
//    $scope.QueryData = function (Query) {
//    	Query.terms = Query.terms
//    	console.log(Query)
//    	GP.createSimpleQuery(Query).then(
//    	       function (content) {
//    	    	   if(content.data.success){
//    	    		   Query = {}
//    	            console.log(content.data)
//    	            $scope.ResultData = content.data.data.searchResults;
//    	    	   }
//    	       }, function (err) {
//    	                // errorCallback
//        });
//        
//    }
//    
//    $scope.advanceQuery = function(){
//		var query = QueryBuilderView.generateXMLOutput;
//		console.log(data);
//	}
//});

routerApp.controller('advanceQueryCtrl', function ($scope, $state, GP, User,Notification) {

	$scope.advanceQuery = function(){
		var query = QueryBuilderView.generateXMLOutput;
		console.log(data);
	}
	
   //all implementation in jquery only
});

routerApp.controller('MapCtrl',function($scope, $state, GP, User,Notification) {
	
	$scope.splitPaneProperties = {};
	$scope.setFirstComponent = function (value) {
		$scope.splitPaneProperties.firstComponentSize = value;
	};
	$scope.setLastComponent = function (value) {
		$scope.splitPaneProperties.lastComponentSize = value;
	};
	
	$scope.dataSearch = true;
	
	$scope.harvests = []
	
	$scope.executeSearch = function(query){
		query.queryXml="<idx:TextSearch><ogc:Literal><idx:Scope>RegistryObject</idx:Scope></ogc:Literal><ogc:Literal><idx:Terms >"+query.terms+"</idx:Terms></ogc:Literal></idx:TextSearch>"
	    	GP.createSimpleQuery(query).then(
	    	       function (content) {
	    	    	   if(content.data.success){
	    	    		   Query = {}
	    	               console.log(content.data)
	    	               $scope.resultData = content.data.data.searchResults;
	    	    	   }
	    	       }, function (err) {
	    	                // errorCallback
	        });
//	        
		
		
	    	
	}
	
	//$scope.executeSearch();
	
	
	
	

					
					var map = {};

					var layers = [];
					var view = new ol.View({
						projection : 'EPSG:4326',
						center : [ 77.695313, 22.917923 ],
						zoom : 6
					});
					var osm = new ol.layer.Tile({
						source : new ol.source.OSM()

					});


					var bing = new ol.layer.Tile(
							{
								source : new ol.source.BingMaps(
										{
											imagerySet : 'Aerial',
											key : 'Av8v3owKmcsVmVMD2jJOD0gLkmBN18MSjBWimMh4t9SaSUK5YIV8ZfCYW0-d8Ogp'
										}),
								visible : false
							});

					layers.push(osm);
					layers.push(bing)

					var popupContainer = document.getElementById('popup');
					var popupContent = document.getElementById('popup-content');
					var closer = document.getElementById('popup-closer');

					var parser = new ol.format.WMSCapabilities();

					/**
					 * Create an overlay to anchor the popup to the map.
					 */
					var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */
					({
						element : popupContainer,
						autoPan : true,
						autoPanAnimation : {
							duration : 250
						}
					}));

					window.app = {};
					var app = window.app;

					/**
					 * @constructor
					 * @extends {ol.control.Control}
					 * @param {Object=}
					 *            opt_options Control options.
					 */
					app.Satelite = function(opt_options) {

						var options = opt_options || {};

						var button = document.createElement('button');
						
						button.innerHTML = 'S';

						// var this_ = this;
						var toggelSatelite = function() {
							// this_.getMap().getView().setRotation(0);
							if (bing.getVisible()) {
								bing.setVisible(false);
							} else {
								bing.setVisible(true);
							}
						};

						button.addEventListener('click', toggelSatelite, false);
						button.addEventListener('touchstart', toggelSatelite,
								false);

//						var layerList = document.createElement('div');
//						layerList.id = "layerList"
						
						var element = document.createElement('div');
						element.className = 'satelite ol-unselectable ol-control';
						element.appendChild(button);
//						element.appendChild(layerList)

						ol.control.Control.call(this, {
							element : element,
							target : options.target
						});

					};
					ol.inherits(app.Satelite, ol.control.Control);

					var mousePositionControl = new ol.control.MousePosition({
				        coordinateFormat: ol.coordinate.createStringXY(4),
				        projection: 'EPSG:4326',
				        // comment the following two lines to have the mouse position
				        // be placed within the map.
				        className: 'custom-mouse-position',
				        target: document.getElementById('position'),
				        undefinedHTML: '&nbsp;'
				      });
					
//					fetch(
//							'/wms?service=WMS&version=1.3.0&request=GetCapabilities')
//							.then(function(response) {
//								return response.text();
//							})
					
					$scope.layerOpacity = {};
					$scope.layerData = [];
					 $scope.layerData.push({name:"OSM",opacity:1})
                     
                     $scope.layerData.push({name:"BING",opacity:0})
					var mapData = {"accessUrl":$state.params.accessUrl};
							GP.createMapView(mapData).then(
									function (content) {
										if(content.data.success){
											
										var doc = content.data.data.layers;	
										var bbox = content.data.data.bbox;
										view = new ol.View({
											projection : 'EPSG:4326',
											center : bbox,
											zoom : 6
										});
                           										
										for ( var i in doc) {
											if (doc[i] != 'undefined' && doc[i] != '') {
												$scope.layerData.push({name:doc[i],opacity:0})
												console.log(doc[i],(i+2))
												
												var layer = new ol.layer.Tile(
														{
															source : new ol.source.TileWMS(
																	{
																		url : '/wmsservice/demo/wms',
																		params : {
																			'LAYERS' : doc[i],
																			'TILED' : true,
																			'VERSION':'1.1.0'
																		},
																		serverType : 'geoserver',
																		crossOrigin : 'anonymous'
																	})
														})
												layer.setVisible(false);
												layers.push(layer);

											}

										}

										var map = new ol.Map({
											controls : ol.control.defaults({
												attributionOptions : /** @type {olx.control.AttributionOptions} */
												({
													collapsible : false
												})
											}).extend([ new app.Satelite(),mousePositionControl, new ol.control.FullScreen()]),
											layers : layers,
											overlays : [ overlay ],
											target : 'mapid',
											view : view
										});

										map.getLayers()
										      .forEach(function(layer, i) {
										    	  $scope.layerOpacity[i] = layer;															
											   });
										
										$scope.addLayer = function(index){
											var layer = $scope.layerOpacity[index]
											layer.setOpacity(1);
											layer.setVisible(true);
										}
										
										$scope.changeOpacity = function(opacity,index){
//											index = index+2
											var layer = $scope.layerOpacity[index]
											layer.setOpacity(opacity)
											if(opacity > 0)
												layer.setVisible(true);
											else
												layer.setVisible(false);
										}

										map.on('singleclick',function(evt) {
															content.innerHTML = '';
															var coordinate = evt.coordinate;
															overlay.setPosition(coordinate);
															var viewResolution = /** @type {number} */
															  (view.getResolution());
															   var str = '';
															   map.getLayers()
																	.forEach(function(layer,i) {
																		console.log(i)
																		if(parseInt(i) > 2){
																				if (layer.getVisible() && (layer.getSource() instanceof ol.source.TileWMS)) {
																					var url = layer
																							.getSource()
																							.getGetFeatureInfoUrl(
																									evt.coordinate,
																									viewResolution,
																									'EPSG:4326',
																									{
																										'INFO_FORMAT' : 'text/html'
																									});
																					console.log(url)
																					if (url) {
																						GP.getFetaureInfo(url).then(
																					    	       function (content) {
																					    	    	   console.log(content);
																					    	    	    
																					    	       }, function (err) {
																					    	                // errorCallback
																					        });
																					       
																						str = str + '<iframe seamless src="'
																								+ url
																								+ '"></iframe>';
																					}
																				}
																		}
																	})
																			
															popupContent.innerHTML = str;
															/*var url = wmsSource.getGetFeatureInfoUrl(
																	evt.coordinate, viewResolution, 'EPSG:4326', {
																		'INFO_FORMAT' : 'text/html'
																	});
															if (url) {
																content.innerHTML = '<iframe seamless src="' + url
																		+ '"></iframe>';
															}*/
														});

										}});

					

					$('#layers li > span').click(function() {
						$(this).siblings('fieldset').toggle();
					});

					/**
					 * Add a click handler to hide the popup.
					 * @return {boolean} Don't follow the href.
					 */
//					closer.onclick = function() {
//						overlay.setPosition(undefined);
//						closer.blur();
//						return false;
//					};

				});

routerApp.controller('MapQueryCtrl',function($scope, $state, GP, User,Notification) {
	
	var map = {};

	var layers = [];
	/**
	 * Define a namespace for the application.
	 */
	window.app = {};
	var app = window.app;
	

	/**
	 * @constructor
	 * @extends {ol.interaction.Pointer}
	 */
	app.Drag = function() {

	  ol.interaction.Pointer.call(this, {
	    handleDownEvent: app.Drag.prototype.handleDownEvent,
	    handleDragEvent: app.Drag.prototype.handleDragEvent,
	    handleMoveEvent: app.Drag.prototype.handleMoveEvent,
	    handleUpEvent: app.Drag.prototype.handleUpEvent
	  });

	  /**
	   * @type {ol.Pixel}
	   * @private
	   */
	  this.coordinate_ = null;

	  /**
	   * @type {string|undefined}
	   * @private
	   */
	  this.cursor_ = 'pointer';

	  /**
	   * @type {ol.Feature}
	   * @private
	   */
	  this.feature_ = null;

	  /**
	   * @type {string|undefined}
	   * @private
	   */
	  this.previousCursor_ = undefined;

	};
	ol.inherits(app.Drag, ol.interaction.Pointer);


	/**
	 * @param {ol.MapBrowserEvent} evt Map browser event.
	 * @return {boolean} `true` to start the drag sequence.
	 */
	app.Drag.prototype.handleDownEvent = function(evt) {
	  var map = evt.map;

	  var feature = map.forEachFeatureAtPixel(evt.pixel,
	      function(feature) {
	        return feature;
	      });

	  if (feature) {
	    this.coordinate_ = evt.coordinate;
	    this.feature_ = feature;
	  }

	  return !!feature;
	};


	/**
	 * @param {ol.MapBrowserEvent} evt Map browser event.
	 */
	app.Drag.prototype.handleDragEvent = function(evt) {
	  var deltaX = evt.coordinate[0] - this.coordinate_[0];
	  var deltaY = evt.coordinate[1] - this.coordinate_[1];

	  var geometry = /** @type {ol.geom.SimpleGeometry} */
	      (this.feature_.getGeometry());
	  geometry.translate(deltaX, deltaY);

	  this.coordinate_[0] = evt.coordinate[0];
	  this.coordinate_[1] = evt.coordinate[1];
	};


	/**
	 * @param {ol.MapBrowserEvent} evt Event.
	 */
	app.Drag.prototype.handleMoveEvent = function(evt) {
	  if (this.cursor_) {
	    var map = evt.map;
	    var feature = map.forEachFeatureAtPixel(evt.pixel,
	        function(feature) {
	          return feature;
	        });
	    var element = evt.map.getTargetElement();
	    if (feature) {
	      if (element.style.cursor != this.cursor_) {
	        this.previousCursor_ = element.style.cursor;
	        element.style.cursor = this.cursor_;
	      }
	    } else if (this.previousCursor_ !== undefined) {
	      element.style.cursor = this.previousCursor_;
	      this.previousCursor_ = undefined;
	    }
	  }
	};


	/**
	 * @return {boolean} `false` to stop the drag sequence.
	 */
	app.Drag.prototype.handleUpEvent = function() {
  	  //alert(this.coordinate_);
		
		$("#point_lat").val(this.coordinate_[0]);
		$("#point_long").val(this.coordinate_[1]);
		

		$scope.filter.lat=$("#point_lat").val();
		$scope.filter.long=$("#point_long").val();
		
		$("#point_radius").val($state.params.radius);
		
		$state.go("mapQuery",{lat:$scope.filter.lat,long:$scope.filter.long,radius:$scope.filter.radius})
		
	  this.coordinate_ = null;
	  this.feature_ = null;
	  return false;
	};


	var pointFeature = new ol.Feature(new ol.geom.Point([$state.params.lat,$state.params.long]));
	
	var view = new ol.View({
		projection : 'EPSG:4326',
		center : [ 77.695313, 22.917923 ],
		zoom : 7
	});
	var osm = new ol.layer.Tile({
		source : new ol.source.OSM()

	});


	var bing = new ol.layer.Tile(
			{
				source : new ol.source.BingMaps(
						{
							imagerySet : 'Aerial',
							key : 'Av8v3owKmcsVmVMD2jJOD0gLkmBN18MSjBWimMh4t9SaSUK5YIV8ZfCYW0-d8Ogp'
						}),
				visible : false
			});

	layers.push(osm);
	layers.push(bing)

	var popupContainer = document.getElementById('popup');
	var popupContent = document.getElementById('popup-content');
	var closer = document.getElementById('popup-closer');

	var parser = new ol.format.WMSCapabilities();

	/**
	 * Create an overlay to anchor the popup to the map.
	 */
	var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */
	({
		element : popupContainer,
		autoPan : true,
		autoPanAnimation : {
			duration : 250
		}
	}));

	

	/**
	 * @constructor
	 * @extends {ol.control.Control}
	 * @param {Object=}
	 *            opt_options Control options.
	 */
	app.Satelite = function(opt_options) {

		var options = opt_options || {};

		var button = document.createElement('button');
		
		button.innerHTML = 'S';

		// var this_ = this;
		var toggelSatelite = function() {
			// this_.getMap().getView().setRotation(0);
			if (bing.getVisible()) {
				bing.setVisible(false);
			} else {
				bing.setVisible(true);
			}
		};

		button.addEventListener('click', toggelSatelite, false);
		button.addEventListener('touchstart', toggelSatelite,
				false);

//		var layerList = document.createElement('div');
//		layerList.id = "layerList"
		
		var element = document.createElement('div');
		element.className = 'satelite ol-unselectable ol-control';
		element.appendChild(button);
//		element.appendChild(layerList)

		ol.control.Control.call(this, {
			element : element,
			target : options.target
		});

	};
	ol.inherits(app.Satelite, ol.control.Control);

	var mousePositionControl = new ol.control.MousePosition({
        coordinateFormat: ol.coordinate.createStringXY(4),
        projection: 'EPSG:4326',
        // comment the following two lines to have the mouse position
        // be placed within the map.
        className: 'custom-mouse-position',
        target: document.getElementById('position'),
        undefinedHTML: '&nbsp;'
      });
	
//	fetch(
//			'/wms?service=WMS&version=1.3.0&request=GetCapabilities')
//			.then(function(response) {
//				return response.text();
//			})
	
	$scope.layerOpacity = {};
	$scope.layerData = [];
	
	$scope.filter = {}
	$scope.filter.lat=$state.params.lat;
	$scope.filter.long=$state.params.long;
	$scope.filter.radius=$state.params.radius;
	
	var queryLayer,querySource;
	
	$scope.queryLayer = function(filter){
						$scope.layerData.push({
							name : "Transport",
							opacity : 0
						})
						// console.log(doc[i],(i+2))

						querySource = new ol.source.TileWMS({
							url : '/wms',
							params : {
								'LAYERS' : "mp_station_query",
								'TILED' : true,
								'viewParams' : "lat:" + filter.lat
										+ ";lon:" + filter.long
										+ ";radius:" + filter.radius
							},
							serverType : 'geoserver',
							crossOrigin : 'anonymous'
						})
						
						queryLayer = new ol.layer.Tile({
							source :querySource
						});
						queryLayer.setVisible(true);
						layers.push(queryLayer);

						

		
	}
	


	$scope.MapData = function(filter){
		$state.go("mapQuery",{lat:filter.lat,long:filter.long,radius:filter.radius})
		//queryLayer.clear();
	}
	
	
	
	$scope.queryLayer($scope.filter);
	
	
	var pointerLayer = new ol.layer.Vector(
			{
				source : new ol.source.Vector({
					features : [ pointFeature ]
				}),
				style : new ol.style.Style(
						{
							image : new ol.style.Icon(
									/** @type {olx.style.IconOptions} */
									({
										anchor : [ 0.5, 46 ],
										anchorXUnits : 'fraction',
										anchorYUnits : 'pixels',
										opacity : 0.95,
										src : 'https://openlayers.org/en/v4.3.1/examples/data/icon.png'
									})),
							stroke : new ol.style.Stroke({
								width : 3,
								color : [ 255, 0, 0, 1 ]
							}),
							fill : new ol.style.Fill({
								color : [ 0, 0, 255, 0.6 ]
							})
						})
			});
	pointerLayer.setVisible(true);
	layers.push(pointerLayer);

	
						var map = new ol.Map({
							controls : ol.control.defaults({
								attributionOptions : /** @type {olx.control.AttributionOptions} */
								({
									collapsible : false
								})
							}).extend([ new app.Satelite(),mousePositionControl, new ol.control.FullScreen()]),
							interactions: ol.interaction.defaults().extend([new app.Drag()]),
							layers : layers,
							overlays : [ overlay ],
							target : 'mapid',
							view : view
						});
						
						map.on('singleclick',function(evt) {
//											content.innerHTML = '';
											var coordinate = evt.coordinate;
											overlay.setPosition(coordinate);
											var viewResolution = /** @type {number} */
											  (view.getResolution());
											   var str = '';
											   map.getLayers()
													.forEach(function(layer,i) {
														if(parseInt(i) > 1){
																if (layer.getVisible() && (layer.getSource() instanceof ol.source.TileWMS)) {
																	var url = layer
																			.getSource()
																			.getGetFeatureInfoUrl(
																					evt.coordinate,
																					viewResolution,
																					'EPSG:4326',
																					{
																						'INFO_FORMAT' : 'text/html'
																					});
																	if (url) {
																	       
																		str = str + '<iframe seamless src="'
																				+ url
																				+ '"></iframe>';
																	}
																}
														}
													})
															
											popupContent.innerHTML = str;
											/*var url = wmsSource.getGetFeatureInfoUrl(
													evt.coordinate, viewResolution, 'EPSG:4326', {
														'INFO_FORMAT' : 'text/html'
													});
											if (url) {
												content.innerHTML = '<iframe seamless src="' + url
														+ '"></iframe>';
											}*/
										});

						

	

	$('#layers li > span').click(function() {
		$(this).siblings('fieldset').toggle();
	});

	/**
	 * Add a click handler to hide the popup.
	 * @return {boolean} Don't follow the href.
	 */
	closer.onclick = function() {
		overlay.setPosition(undefined);
		closer.blur();
		return false;
	};

});

routerApp
		.controller(
				'ComplexQueryCtrl',
				function($scope, $state, GP, User, Notification) {

					var map = {};

					var layers = [];
					/**
					 * Define a namespace for the application.
					 */
					window.app = {};
					var app = window.app;

					var view = new ol.View({
//						projection : 'EPSG:4326',
						center : [ 77.695313, 22.917923 ],
						zoom : 2
					});
					var osm = new ol.layer.Tile({
						source : new ol.source.OSM()

					});

					var bing = new ol.layer.Tile(
							{
								source : new ol.source.BingMaps(
										{
											imagerySet : 'Aerial',
											key : 'Av8v3owKmcsVmVMD2jJOD0gLkmBN18MSjBWimMh4t9SaSUK5YIV8ZfCYW0-d8Ogp'
										}),
								visible : false
							});

					layers.push(osm);
					layers.push(bing)

					var popupContainer = document.getElementById('popup');
					var popupContent = document.getElementById('popup-content');
					var closer = document.getElementById('popup-closer');

					var parser = new ol.format.WMSCapabilities();
					
					var replacer = function(key, value) {
				        if (value.geometry) {
				          var type;
				          var rawType = value.type;
				          var geometry = value.geometry;

				          if (rawType === 1) {
				            type = 'MultiPoint';
				            if (geometry.length == 1) {
				              type = 'Point';
				              geometry = geometry[0];
				            }
				          } else if (rawType === 2) {
				            type = 'MultiLineString';
				            if (geometry.length == 1) {
				              type = 'LineString';
				              geometry = geometry[0];
				            }
				          } else if (rawType === 3) {
				            type = 'Polygon';
				            if (geometry.length > 1) {
				              type = 'MultiPolygon';
				              geometry = [geometry];
				            }
				          }

				          return {
				            'type': 'Feature',
				            'geometry': {
				              'type': type,
				              'coordinates': geometry
				            },
				            'properties': value.tags
				          };
				        } else {
				          return value;
				        }
				      };
				      
				      var tilePixels = new ol.proj.Projection({
				          code: 'TILE_PIXELS',
				          units: 'tile-pixels'
				        });

					/**
					 * Create an overlay to anchor the popup to the map.
					 */
					var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */
					({
						element : popupContainer,
						autoPan : true,
						autoPanAnimation : {
							duration : 250
						}
					}));

					/**
					 * @constructor
					 * @extends {ol.control.Control}
					 * @param {Object=}
					 *            opt_options Control options.
					 */
					app.Satelite = function(opt_options) {

						var options = opt_options || {};

						var button = document.createElement('button');

						button.innerHTML = 'S';

						// var this_ = this;
						var toggelSatelite = function() {
							// this_.getMap().getView().setRotation(0);
							if (bing.getVisible()) {
								bing.setVisible(false);
							} else {
								bing.setVisible(true);
							}
						};

						button.addEventListener('click', toggelSatelite, false);
						button.addEventListener('touchstart', toggelSatelite,
								false);

						// var layerList = document.createElement('div');
						// layerList.id = "layerList"

						var element = document.createElement('div');
						element.className = 'satelite ol-unselectable ol-control';
						element.appendChild(button);
						// element.appendChild(layerList)

						ol.control.Control.call(this, {
							element : element,
							target : options.target
						});

					};
					ol.inherits(app.Satelite, ol.control.Control);
					var mousePositionControl = new ol.control.MousePosition({
				        coordinateFormat: ol.coordinate.createStringXY(4),
				        projection: 'EPSG:4326',
				        // comment the following two lines to have the mouse position
				        // be placed within the map.
				        className: 'custom-mouse-position',
				        target: document.getElementById('position'),
				        undefinedHTML: '&nbsp;'
				      });
					$scope.layerOpacity = {};
					$scope.layerData = [];

					var queryLayer, querySource;

//					$scope.queryLayer = function(filter) {
//						$scope.layerData.push({
//							name : "Transport",
//							opacity : 0
//						})

						querySource = new ol.source.TileWMS({
							url : '/wmsservice/wms',
							params : {
								'LAYERS' : "mp_station_query",
								'TILED' : true
//								'viewParams' : "lat:" + filter.lat + ";lon:"
//										+ filter.long + ";radius:"
//										+ filter.radius
							},
							serverType : 'geoserver',
							crossOrigin : 'anonymous'
						})

						queryLayer = new ol.layer.Tile({
							source : querySource
						});
						queryLayer.setVisible(true);
						layers.push(queryLayer);

//					}

				      
					var map = new ol.Map({
						controls : ol.control.defaults({
							attributionOptions : /** @type {olx.control.AttributionOptions} */
							({
								collapsible : false
							})
						}).extend(
								[ new app.Satelite(), mousePositionControl,
										new ol.control.FullScreen() ]),
						
						layers : layers,
						overlays : [ overlay ],
						target : 'mapid',
						view : view
					});
					var url = 'http://localhost:9191/css/geo.json';
				      fetch(url).then(function(response) {
				        return response.json();
				      }).then(function(json) {
				        var tileIndex = geojsonvt(json, {
				          extent: 4096,
				          debug: 1
				        });
				        var vectorSource = new ol.source.VectorTile({
				          format: new ol.format.GeoJSON(),
				          tileLoadFunction: function(tile) {
				            var format = tile.getFormat();
				            var tileCoord = tile.getTileCoord();
				            var data = tileIndex.getTile(tileCoord[0], tileCoord[1], -tileCoord[2] - 1);

				            var features = format.readFeatures(
				                JSON.stringify({
				                  type: 'FeatureCollection',
				                  features: data ? data.features : [],
				                dataProjection: 'EPSG:4326', featureProjection: 'EPSG:4326'
				                }, replacer));
				            tile.setLoader(function() {
				              tile.setFeatures(features);
				              tile.setProjection(tilePixels);
				            });
				          },
				          url: 'data:' // arbitrary url, we don't use it in the tileLoadFunction
				        });
				        var vectorLayer = new ol.layer.VectorTile({
				          source: vectorSource
				        });
				        //map.addLayer(vectorLayer);
				        vectorLayer.setVisible(true);
				        map.addLayer(vectorLayer);
//				        layers.push(vectorLayer);
				      });
				      
					map
							.on(
									'singleclick',
									function(evt) {
										// content.innerHTML = '';
										var coordinate = evt.coordinate;
										overlay.setPosition(coordinate);
										var viewResolution = /** @type {number} */
										(view.getResolution());
										var str = '';
										map
												.getLayers()
												.forEach(
														function(layer, i) {
															if (parseInt(i) > 1) {
																if (layer
																		.getVisible()
																		&& (layer
																				.getSource() instanceof ol.source.TileWMS)) {
																	var url = layer
																			.getSource()
																			.getGetFeatureInfoUrl(
																					evt.coordinate,
																					viewResolution,
																					'EPSG:4326',
																					{
																						'INFO_FORMAT' : 'text/html'
																					});
																	if (url) {

																		str = str
																				+ '<iframe seamless src="'
																				+ url
																				+ '"></iframe>';
																	}
																}
															}
														})

										popupContent.innerHTML = str;
										/*
										 * var url =
										 * wmsSource.getGetFeatureInfoUrl(
										 * evt.coordinate, viewResolution,
										 * 'EPSG:4326', { 'INFO_FORMAT' :
										 * 'text/html' }); if (url) {
										 * content.innerHTML = '<iframe
										 * seamless src="' + url + '"></iframe>'; }
										 */
									});
					


					$('#layers li > span').click(function() {
						$(this).siblings('fieldset').toggle();
					});

					/**
					 * Add a click handler to hide the popup.
					 * @return {boolean} Don't follow the href.
					 */
					closer.onclick = function() {
						overlay.setPosition(undefined);
						closer.blur();
						return false;
					};

				});


routerApp.controller('OrganisationCtrl', function ($scope, $state, GP,Notification) {

    $scope.organisation = {}
    $scope.organisation.emailAddress = [];
    $scope.organisation.phoneNumbers = [];
    $scope.organisation.address = [];
    $scope.organisation.nodalOfficer = {};
    $scope.organisation.admin = {};


    $scope.plusEmailAddress = function () {
        $scope.organisation.emailAddress.push({})
    }
    $scope.plusPhoneNumber = function () {
        $scope.organisation.phoneNumbers.push({})
    }
    $scope.plusAddress = function () {
        $scope.organisation.address.push({})
    }

    $scope.minusEmailAddress = function (index) {
        $scope.organisation.emailAddress.splice(index, 1)
    }
    $scope.minusPhoneNumber = function (index) {
        $scope.organisation.phoneNumbers.splice(index, 1)
    }
    $scope.minusAddress = function (index) {
        $scope.organisation.address.splice(index, 1)
    }

    $scope.createOraganistion = function (organisation) {
    	

        GP.createOrganisation(organisation).then(
            function (content) {
            	$scope.showModal = !$scope.showModal;
            }, function (err) {
                // errorCallback
            });

        console.log(organisation)
    }    

    $scope.deleteOrganisation = function (id, index) {
        var ids = [id]
        GP.deleteOrganisation(ids).then(
            function (content) {
                $scope.listOrganisations()
                $scope.users.splice(index, 1)
                data = {}
            }, function (err) {
                // errorCallback
            });
    }


    $scope.listOrganisations = function () {
        GP.getByQueryJson('&request=Query&service=CSW-ebRIM&version=1.0.1&qid=urn:x-indicio:def:stored-query:all-objects-of-type&resultType=results&outputFormat=application/json&startPosition=1&type=urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Organization').then(
            function (content) {
                if (content.data.data) {
                    $scope.organisations = content.data.data.searchResults
                }
            }, function (data) {
                // errorCallback
            });
    }
    $scope.listOrganisations()
    
    GP.listClassification("DataAccess").then(
  	       function (content) {
 	    	   if(content.data.success){
 	    		   $scope.dataAccesses = content.data.data;
 	    	   }
 	       }, function (err) {
 	                // errorCallback
     });


});

routerApp.controller('HarvestListCtrl', function ($scope, $state, GP,Notification) {
    
	GP.listHarvest().then(
            function (content) {
            	$scope.harvests = content.data.data;
                console.log(content.data)
            }, function (err) {
                // errorCallback
            });  
	
	$scope.syncHarvest = function(id){
		GP.syncHarvest(id).then(
	            function (content) {
	            	console.log(content.data)
	            }, function (err) {
	                // errorCallback
	            });
	}
});

routerApp.controller('InactiveOrganisationCtrl', function ($scope, $state, GP,Notification) {
    
	GP.inactiveOrganisation().then(
            function (content) {
            	$scope.organisations = content.data.data;
                console.log(content.data)
            }, function (err) {
                // errorCallback
            });
    
    $scope.approve =function(id){
        GP.approveOrganisation(id).then(
    	            function (content) {
    	            	if(content.data.success){
    	    	    		  Notification.info("Successfully Approved");
    	    	    		   $state.go("app.organisationList")
    	    	    	   }
    	    	    	   else{
    	    	    		   Notification.error("Some error occured");	    
    	    	    	   }
    	                console.log(content)
    	            }, function (data) {
    	                // errorCallback
    	            });

    }
  
});


routerApp.controller('ViewRecordCtrl', function ($scope, $state,$anchorScroll,$location, GP,Notification) {

    userId = $state.params.id
    
    $scope.gotoAnchor = function(x) {
        var newHash = 'anchor' + x;
        if ($location.hash() !== newHash) {
          // set the $location.hash to `newHash` and
          // $anchorScroll will automatically scroll to it
          $location.hash('anchor' + x);
        } else {
          // call $anchorScroll() explicitly,
          // since $location.hash hasn't changed
          $anchorScroll();
        }
      };

    GP.getByQueryJson('request=GetRecordById&service=CSW-ebRIM&version=1.0.1&id=' + userId + '&outputFormat=application/json&elementSetName=full').then(
        function (content) {
            $scope.user = content.data.data.searchResults[0]
        }, function (data) {
            // errorCallback
        });

    GP.getByQueryJson('request=Query&service=CSW-ebRIM&version=1.0.1&qid=urn:x-indicio:def:stored-query:classified-tuples&resultType=results&outputFormat=application/json&elementSetName=full&maxRecords=100&id=' + userId).then(
        function (content) {
            $scope.classifiedTuples = []
            if (content.data.data.searchResults) {
                for (var i = 0; i < content.data.data.searchResults.length; i++) {
                    var registryObjectList = content.data.data.searchResults[i].registryObjectList
                    if (registryObjectList)
                        $scope.classifiedTuples.push(registryObjectList[1])
                }
            }
        }, function (data) {
            // errorCallback
        });

    GP.getByQueryJson('request=Query&service=CSW-ebRIM&version=1.0.1&qid=urn:x-indicio:def:stored-query:associated-tuples&resultType=results&outputFormat=application/json&id=' + userId).then(
        function (content) {
            $scope.associationTuples = []
            if (content.data.data.searchResults) {
                for (var i = 0; i < content.data.data.searchResults.length; i++) {
                    var registryObjectList = content.data.data.searchResults[i].registryObjectList
                    if (registryObjectList) {
                        var association = {}
                        association.type = registryObjectList[0].associationType
                        association.id = registryObjectList[1].id
                        association.name = registryObjectList[1].name
                        association.objectType = registryObjectList[1].objectType
                        $scope.associationTuples.push(association);
                    }
                }
            }
        }, function (data) {
            // errorCallback
        });

    GP.getByQueryJson('request=Query&service=CSW-ebRIM&version=1.0.1&qid=urn:x-indicio:def:stored-query:audit-trail&resultType=results&outputFormat=application/json&id=' + userId).then(
        function (content) {
            $scope.audits = content.data.data.searchResults
        }, function (data) {
            // errorCallback
        });


});

routerApp.controller('UserCtrl', function ($scope,$state, GP, Notification) {
	
	$scope.menuNames = [
		'ListOrganisation',
		'InactiveOrganisation',
		'CreateOrganisation',
		'ListRoles',
		'CreateRole',
		'SubmitUser',
		'CreateUser',
		'MapLayer',
		'SimpleQuery',
		'AdvanceQuery',
		'Harvest',
		'ListUser',
		'ApproveUser']
	
	$scope.userRole ={}
	$scope.userRole.permission = {} 
	$scope.rolePermission = function(userRole){
		var name = userRole.role.name
		GP.createRolePermission(name,userRole.permission).then(
	            function (content) {
	                if (content.data.data) {
	                	console.log(content.data)
	                }
	                Notification.info(content.data.message);
	            }, function (data) {
	                // errorCallback
	            });
		console.log(name,userRole)
	}
    
    GP.listClassification("DataAccess").then(
 	       function (content) {
	    	   if(content.data.success){
	    		   $scope.dataAccesses = content.data.data;
	    	   }
	       }, function (err) {
	                // errorCallback
    });
	
	$scope.listRolePermission = function(userRole){
		var name = userRole.role.name
		GP.listRolePermission(name).then(
	            function (content) {
	                if (content.data.data) {
	                	$scope.userRole.permission = content.data.data;
	                }
	            }, function (data) {
	                // errorCallback
	            });
	}
	
	$scope.nextPage = function(user){
		$scope.isNext = true;
		$scope.user = user;
	}
	$scope.prevPage = function(user){
		$scope.isNext = false;
		$scope.user = user;
	}

    $scope.listRoles = function () {
        var roles = []
        GP.getByQueryJson('request=GetAllRoles&outputFormat=application/json').then(
            function (content) {
                if (content.data.data) {
                    $scope.userRoles = content.data.data.userRoles
                }
            }, function (data) {
                // errorCallback
            });
    }
    $scope.listRoles()

    $scope.createRole = function (data) {
        GP.createRole(data).then(
            function (content) {
            	if(content.data.success){
            	   Notification.info("Role created successfully");
            	   $state.go("app.list_role")
            	}
            	else{
            		 Notification.info(content.data.message);
            	}
                data = {}
            }, function (err) {
                // errorCallback
            });
    }
    
    $scope.deleteRole = function (name, index) {
        data = { "roleName": name }
        GP.deleteRole(data).then(
            function (content) {
                $scope.userRoles.splice(index, 1)
                console.log(content)
            }, function (data) {
                // errorCallback
            });
    }
    
    $scope.aadhar = "2";
    $scope.pan = "2";
    $scope.validateIdentityProof = function(name,value,length){
       if(value.length == length){ 	
    	 GP.validateIdentityProof(name,value).then(
                function (content) {
                	if(content.data.success){
                		if(name == "AADHAR")
                 		   $scope.aadhar = "1";
                		if(name == "PAN")
                 		   $scope.pan = "1";
    	                  Notification.info(content.data.message);
    	            	}
    	            	else{
                    		if(name == "PAN")
                     		   $scope.pan = "0";
                    		if(name == "AADHAR")
                     		   $scope.aadhar = "0";
    		              Notification.error(content.data.message);	            		
    	            	}
                }, function (err) {
                    // errorCallback
                });
    	}
    }

    $scope.createUser = function (data) {
        GP.createUser(data).then(
            function (content) {
            	if(content.data.success){
             	   Notification.info("User created successfully");
             	   $state.go("app.user_list")
             	}
             	else{
             		 Notification.info(content.data.message);
             	}
                data = {}
            }, function (err) {
                // errorCallback
            });
    }

    $scope.deleteUser = function (id, index) {
        var ids = [id]
        GP.deleteUser(ids).then(
            function (content) {
                $scope.listUsers()
                $scope.users.splice(index, 1)
                data = {}
            }, function (err) {
                // errorCallback
            });
    }

    $scope.approveUser = function (user,index) {
        GP.approveUser('U-'+user.id).then(
            function (content) {
            	if(content.data.success){
            		$scope.approveUsers.splice(index,1);
            		$scope.users.push(user);
            		Notification.info("User Approved successfully");
            		user.status = 3;
            	}
            	else{
           		 Notification.info(content.data.message);                		
           	}
            }, function (data) {
                // errorCallback
            });

    }

    $scope.context = function (user) {
        GP.getByQueryJson('&request=GetUserContext&userId=' + user.id + '&outputFormat=application/json').then(
            function (content) {
                user.context = content.data.data
            }, function (data) {
                // errorCallback
            });
    }
    
    $scope.submitUser = function (id,index) {
    	GP.submitUser(id).then(
                function (content) {
                	if(content.data.success){
                		$scope.approveUsers.splice(index,1);
                		Notification.info("User Activated successfully");
                	}
                	else{
                		 Notification.info(content.data.message);                		
                	}
                }, function (data) {
                    // errorCallback
                });
    }

    $scope.listUsers = function () {
    	
    	$scope.users = []
    	$scope.approveUsers = []
    	$scope.submitUsers = []
    	
    	GP.listUser().then(function (content) {
          if (content.data.data) {
        	  for(var i=0;i<content.data.data.length;i++){
        		  var user = content.data.data[i]
        		  if(user.status == 3){
        			  $scope.users.push(user)
        		  }
        		  else if(user.status == 2){
        			  $scope.approveUsers.push(user)
        		  }
        		  else if(user.status == 1){
        			  $scope.submitUsers.push(user)
        		  }
        	  }
             content.data.data;
          }
         }, function (data) {
          // errorCallback
        });
    }
    $scope.listUsers()

});


routerApp.constant("HOST_NAME", "")

routerApp.factory('User', ['HOST_NAME', function (HOST_NAME) {
    return {
        save: function (user) {
            localStorage.setItem("user", JSON.stringify(user));
        },
        get: function () {
            var user = localStorage.getItem("user");
            if (user)
                return JSON.parse(user);
        },
    };
}])

routerApp.factory('GP', ['$http', 'HOST_NAME', function ($http, HOST_NAME) {
    return {

    	login: function (data, success, error) {
            return $http.post(HOST_NAME + '/token', data)
        },
        verifyOtp : function (data, success, error) {
            return $http.post(HOST_NAME + '/verifyotp/'+data.otp, data)
        },
        forgotPassword: function (username, success, error) {
            return $http.post(HOST_NAME + '/forgotPassword/'+username)
        },
        recoverPassword: function (token,user, success, error) {
            return $http.post(HOST_NAME + '/recoverPassword/'+token,user)
        },
        organisationList: function (success, error) {
            return $http.get(HOST_NAME + '/organisation/list')
        },
        register: function (data, success, error) {
            return $http.post(HOST_NAME + '/register', data)
        },
        acceptInvite: function (token, data, success, error) {
            return $http.post(HOST_NAME + '/inviteUserAdd/'+token , data)
        },
        createOrganisation: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/registry/createOrganisation', data)
        },
        inactiveOrganisation: function (success, error) {
            return $http.get(HOST_NAME + '/secure/registry/list/inactiveOrganisation')
        },
        createUser: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/addUser', data)
        },
        submitUser: function (id, success, error) {
            return $http.post(HOST_NAME + '/secure/submitUser/'+id)
        },
        listUser: function (data, success, error) {
            return $http.get(HOST_NAME + '/secure/listUser', data)
        },
        approveOrganisation: function (id, success, error) {
            return $http.post(HOST_NAME + '/secure/registry/approveOrganisation/'+id)
        },
        approveUser: function (id, success, error) {
            return $http.post(HOST_NAME + '/secure/approve/'+id)
        },
        createHarvest: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/harvest',data)
        },
        createSimpleQuery: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/query/simple',data)
        },
        createAdvanceQuery: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/query/advance',data)
        },
        createMapView: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/map/view',data)
        },
        deleteUser: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/delete', data)
        },
        deleteOrganisation: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/delete', data)
        },
        createRole: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/registry/createRole', data)
        },
        deleteRole: function (data, success, error) {
            return $http.post(HOST_NAME + '/secure/registry/deleteRole', data)
        },
        listRolePermission:function(name, success, error){
        	return $http.get(HOST_NAME+'/secure/registry/role/list/'+name)
        },
        createRolePermission:function(name,data, success, error){
        	return $http.post(HOST_NAME+'/secure/registry/role/add/'+name,data)
        },
        getByQueryJson: function (param) {
            return $http.get(HOST_NAME + '/secure/query?' + param)
        },
        getFetaureInfo: function (url) {
            return $http.get(url)
        },
        listClassification: function (name) {
            return $http.post(HOST_NAME + '/secure/list/classification/'+name)
        },
        listHarvest: function (name) {
            return $http.get(HOST_NAME + '/secure/list/harvest')
        },
        syncHarvest: function (id) {
            return $http.post(HOST_NAME + '/secure/sync/harvest/'+id)
        },
        validateIdentityProof: function (name,value) {
            return $http.get(HOST_NAME + '/validateIdentityProof/'+name+'/'+value)
        }

    }
}]);


routerApp.filter('objectValue', [function () {
    return function (string) {
        if (!angular.isString(string)) {
            return string;
        }
        if (string.length > 0) {
            var n = string.lastIndexOf(":");
            string = string.substring(n + 1, string.length);
        }
        return string;
    };
}])

routerApp.filter('reverse', function () {
    return function (items) {
        if (items)
            items = items.slice().reverse();
        return items;
    };
});

routerApp.directive("limitTo", [function() {
    return {
        restrict: "A",
        link: function(scope, elem, attrs) {
            var limit = parseInt(attrs.limitTo);
            angular.element(elem).on("keypress", function(e) {
                if (this.value.length == limit) e.preventDefault();
            });
        }
    }
}])

routerApp.directive('ngVisibleTo', ['User', function (User) {
	return {
		restrict: 'A',
		link: function ($scope, element, attrs) {
			var prevDisp = element.css('display'), userRoles, visibleTo;
			$scope.user = User.get();
			userRoles = $scope.user.roles;
			$scope.$watch('user', function (user) {
				if (user.roles) {
					userRoles = user.roles;
				}
				updateCSS();
			}, true);
			attrs.$observe('ngVisibleTo', function (al) {
				if (al) visibleTo = al;
				updateCSS();
			});

			function updateCSS() {
				if (userRoles && visibleTo) {
					var show = false;
					for(var i=0;i<userRoles.length;i++){
						role = userRoles[i];
						if(visibleTo.indexOf(role)>-1){
							show = true;
							break;
						}
					}
					if (show) {
						element.css('display', prevDisp);
					} else {
						element.css('display', 'none');
					}
				}
			}
		}
	};
}])


routerApp.directive('modal', function () {
    return {
      template: '<div class="modal fade">' + 
          '<div class="modal-dialog">' + 
            '<div class="modal-content">' + 
              '<div class="modal-header">' + 
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' + 
                '<h4 class="modal-title">{{ title }}</h4>' + 
              '</div>' + 
              '<div class="modal-body" ng-transclude></div>' + 
            '</div>' + 
          '</div>' + 
        '</div>',
      restrict: 'E',
      transclude: true,
      replace:true,
      scope:true,
      link: function postLink(scope, element, attrs) {
        scope.title = attrs.title;

        scope.$watch(attrs.visible, function(value){
          if(value == true)
            $(element).modal('show');
          else
            $(element).modal('hide');
        });

        $(element).on('shown.bs.modal', function(){
          scope.$apply(function(){
            scope.$parent[attrs.visible] = true;
          });
        });

        $(element).on('hidden.bs.modal', function(){
          scope.$apply(function(){
            scope.$parent[attrs.visible] = false;
          });
        });
      }
    };
  });


