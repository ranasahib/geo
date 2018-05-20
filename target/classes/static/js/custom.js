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

var container = document.getElementById('popup');
var content = document.getElementById('popup-content');
var closer = document.getElementById('popup-closer');

var parser = new ol.format.WMSCapabilities();

/**
 * Create an overlay to anchor the popup to the map.
 */
var overlay = new ol.Overlay(/** @type {olx.OverlayOptions} */
({
	element : container,
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
 * @param {Object=} opt_options Control options.
 */
app.Satelite = function(opt_options) {

	var options = opt_options || {};

	var button = document.createElement('button');
	button.innerHTML = 'S';

	//          var this_ = this;
	var toggelSatelite = function() {
		//this_.getMap().getView().setRotation(0);
		if (bing.getVisible()) {
			bing.setVisible(false);
		} else {
			bing.setVisible(true);
		}
	};

	button.addEventListener('click', toggelSatelite, false);
	button.addEventListener('touchstart', toggelSatelite, false);

	var element = document.createElement('div');
	element.className = 'satelite ol-unselectable ol-control';
	element.appendChild(button);

	ol.control.Control.call(this, {
		element : element,
		target : options.target
	});

};
ol.inherits(app.Satelite, ol.control.Control);

fetch(
		'http://localhost:8090/wms?service=WMS&version=1.3.0&request=GetCapabilities')
		.then(function(response) {
			return response.text();
		})
		.then(
				function(text) {
					var doc = parser.read(text);
					
					var str = '';
					for ( var i in doc.Capability.Layer["Layer"]) {
						if (doc.Capability.Layer["Layer"][i].Name != 'undefined'
								&& doc.Capability.Layer["Layer"][i].Name != '') {
							//layers.push(doc.Capability.Layer["Layer"][i].Name);
							var j = 2 + parseInt(i);
							var li = document.createElement("li")
							str = '<span>'
									+ doc.Capability.Layer["Layer"][i].Name
									+ '</span><fieldset id="layer'
									+ j
									+ '" style="display: block;">'
									+ '<label class="checkbox" for="visible'
									+ j
									+ '"> <input	id="visible'
									+ j
									+ '" class="visible" type="checkbox"></input>'
									+ 'visibility</label> <label>opacity</label> <input class="opacity" type="range" min="0" max="1" step="0.01"></input>'
									+ '</fieldset>';
							li.innerHTML = str;
							document.getElementById('layerList').append(li);

							layers
									.push(new ol.layer.Tile(
											{
												source : new ol.source.TileWMS(
														{
															url : 'http://localhost:8090/wms',
															params : {
																'LAYERS' : 'mp:'
																		+ doc.Capability.Layer["Layer"][i].Name,
																'TILED' : true
															},
															serverType : 'geoserver',
															crossOrigin : 'anonymous'
														})
											}));

						}

					}

					var map = new ol.Map({
						controls : ol.control.defaults({
							attributionOptions : /** @type {olx.control.AttributionOptions} */
							({
								collapsible : false
							})
						}).extend([ new app.Satelite() ]),
						layers : layers,
						overlays : [ overlay ],
						target : 'mapid',
						view : view
					});

					map.getLayers().forEach(function(layer, i) {
						bindInputs('#layer' + i, layer);
						if (layer instanceof ol.layer.Group) {
							layer.getLayers().forEach(function(sublayer, j) {
								bindInputs('#layer' + i + j, sublayer);
							});
						}
					});

					map.on('singleclick', function(evt) {
						content.innerHTML = '';
						var coordinate = evt.coordinate;
						overlay.setPosition(coordinate);
						var viewResolution = /** @type {number} */
						(view.getResolution());
						var str = '';
						map.getLayers().forEach(function(layer, i) {
							
							if (layer.getVisible() && (layer.getSource() instanceof ol.source.TileWMS)) {
								var url = layer.getSource().getGetFeatureInfoUrl(
										evt.coordinate, viewResolution, 'EPSG:4326', {
											'INFO_FORMAT' : 'text/html'
										});
								if (url) {
									str = str + '<iframe seamless src="' + url
											+ '"></iframe>';
								}
							}
						})
						content.innerHTML = str;
						/*var url = wmsSource.getGetFeatureInfoUrl(
								evt.coordinate, viewResolution, 'EPSG:4326', {
									'INFO_FORMAT' : 'text/html'
								});
						if (url) {
							content.innerHTML = '<iframe seamless src="' + url
									+ '"></iframe>';
						}*/
					});

				});

function bindInputs(layerid, layer) {
	var visibilityInput = $(layerid + ' input.visible');
	visibilityInput.on('change', function() {
		layer.setVisible(this.checked);
	});
	visibilityInput.prop('checked', layer.getVisible());

	var opacityInput = $(layerid + ' input.opacity');
	opacityInput.on('input change', function() {
		layer.setOpacity(parseFloat(this.value));
	});
	opacityInput.val(String(layer.getOpacity()));
}

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

/*var url1='http://localhost:8090/wfs?service=WFS&version=2.0.0&request=GetFeature&typeName=mp:MP-DIST-FOREST-COV&maxFeatures=50&outputFormat=application%2Fjson&srsname=EPSG:4326&';

 var vectorSource1 = new ol.source.Vector({
 format: new ol.format.GeoJSON(),
 url: function(extent) {
 return url1+'bbox=' + extent.join(',') + ',EPSG:4326';
 },
 strategy: ol.loadingstrategy.bbox
 });
 var url2 = 'http://localhost:8090/wfs?service=WFS&version=2.0.0&request=GetFeature&typeName=mp:railways&maxFeatures=50&outputFormat=application%2Fjson&srsname=EPSG:4326&';

 var vectorSource2 = new ol.source.Vector({
 format: new ol.format.GeoJSON(),
 url: function(extent) {
 return url2+'bbox=' + extent.join(',') + ',EPSG:4326';
 },
 strategy: ol.loadingstrategy.bbox
 });

 var vector1 = new ol.layer.Vector({
 source: vectorSource1,
 style: new ol.style.Style({
 stroke: new ol.style.Stroke({
 color: 'rgba(0, 0, 255, 1.0)',
 width: 2
 })
 })
 });

 var vector2 = new ol.layer.Vector({
 source: vectorSource2,
 style: new ol.style.Style({
 stroke: new ol.style.Stroke({
 color: 'rgba(255, 0, 0, 1.0)',
 width: 2
 })
 })
 });
 var wmsSource = new ol.source.TileWMS({
 url: 'http://localhost:8090/wms',
 params: {'LAYERS': 'mp:MP-DIST-FOREST-COV', 'TILED': true},
 serverType: 'geoserver',
 crossOrigin: 'anonymous'
 });

 var wmsLayer = new ol.layer.Tile({
 source: wmsSource
 });



 var layerGrp =  new ol.layer.Group({
 layers: [wmsLayer,vector2]});





 var selectPointerMove = new ol.interaction.Select({
 condition: ol.events.condition.pointerMove
 });

 map.addInteraction(selectPointerMove);









 map.on('pointermove', function(evt) {
 if (evt.dragging) {
 return;
 }
 var pixel = map.getEventPixel(evt.originalEvent);
 var hit = map.forEachLayerAtPixel(pixel, function() {
 return true;
 });
 map.getTargetElement().style.cursor = hit ? 'pointer' : '';
 });*/

