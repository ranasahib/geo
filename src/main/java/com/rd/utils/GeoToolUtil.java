package com.rd.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.geotools.data.Query;
import org.geotools.data.ows.Layer;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.data.wms.WMSUtils;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.xml.MetadataURL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

import com.rd.dto.LayerDTO;
import com.rd.dto.PropertyDTO;

public class GeoToolUtil {

	Map<String, String> metaData = null;
	WMSCapabilities capabilities = null;
	Map<String, LayerDTO> layersMap = null;
	String uri;

	public GeoToolUtil(String uri) {
		super();
		try {
			URL url = new URL(uri);
			this.uri = url.getProtocol()+"://"+url.getHost()+":"+url.getPort()+"/geoserver/ows";
		} catch (MalformedURLException e) {
			this.uri = uri;
			e.printStackTrace();
		} 
	}

	public static void main(String args[]) {
		GeoToolUtil geoTool = new GeoToolUtil(
				"http://115.118.127.127:8091/geoserver/ows?service=WMS&");
		geoTool.importLayerToDB();
		// System.out.println(geoTool.fetchMetaData());
		// System.out.println(geoTool.getLayers());
		// System.out.println(geoTool.importLayerToDB());
	}

	public WMSCapabilities getWMSCapability() {

		if (null != capabilities) {
			return capabilities;
		}
		URL url = null;

		if (null == uri || uri.trim().isEmpty()) {
			uri = "http://115.118.127.127:8091/geoserver/ows";
		}
		try {
			url = new URL(uri + "?service=wms&version=1.3.0&request=GetCapabilities");
		} catch (MalformedURLException e) {

		}

		WebMapServer wms = null;

		try {
			wms = new WebMapServer(url);
			capabilities = wms.getCapabilities();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return capabilities;
	}

	

	public Map<String,LayerDTO> getLayers() {
		if(null != layersMap && !layersMap.isEmpty()){
			return layersMap;
		}
		layersMap = new HashMap<String,LayerDTO>();
		
		Map<String, Serializable> connectionParameters = new HashMap<String, Serializable>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", uri+"?service=wfs&version=1.1.0&request=GetCapabilities");
		int count = 0;
		WFSDataStoreFactory dsf = new WFSDataStoreFactory();
		try {
			WFSDataStore dataStore = dsf.createDataStore(connectionParameters);
			List<Name> names = dataStore.getNames();
			for (Name name : names) {				
				LayerDTO layerDTO = new LayerDTO();				
				String layerName = name.getLocalPart();
				SimpleFeatureSource source = dataStore.getFeatureSource(layerName);
				SimpleFeatureType schema = source.getSchema();
				Query query = new Query(schema.getTypeName(), Filter.INCLUDE);
				count = source.getCount(query);

				layerDTO.setName(name.toString());
				layerDTO.setCount(count);

				List<PropertyDTO> propertyDTOs = new ArrayList<>();
				List<AttributeDescriptor> attributeDescriptors = schema.getAttributeDescriptors();
				for (AttributeDescriptor attributeDescriptor : attributeDescriptors) {
					PropertyDTO propertyDTO = new PropertyDTO();
					propertyDTO.setName(attributeDescriptor.getName().toString());
					propertyDTO.setDefaultValue((String) attributeDescriptor.getDefaultValue());
					propertyDTO.setType(attributeDescriptor.getType().getBinding().getSimpleName());
					propertyDTOs.add(propertyDTO);
				}
				layerDTO.setProperties(propertyDTOs);
				layerName = layerName.substring(layerName.indexOf(":") + 1, layerName.length());
				layersMap.put(layerName, layerDTO);
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		
		return layersMap;
	}

	public Map<String, String> fetchMetaData() {
		if (null != metaData) {
			return metaData;
		}
		try {
			WMSCapabilities capabilities = getWMSCapability();
			if (null != capabilities) {
				metaData = new HashMap<String, String>();
				metaData.put("updateSequence", capabilities.getUpdateSequence());
				metaData.put("boundingBox", capabilities.getLayer().getBoundingBoxes().get("CRS:84").toString());
				metaData.put("baseUrl", capabilities.getService().getOnlineResource().toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return metaData;

	}

	public Map<String, String> importLayerToDB() {

		int totalSuccess = 0;
		int totalFailed = 0;

//		List<LayerDTO> layers = 
		layersMap = getLayers();
		if (null != layersMap && layersMap.size() > 0) {
			for (String layerName : layersMap.keySet()) {
//				String layerName = layerDTO.getName().contains(":")
//						? layerDTO.getName().substring(layerDTO.getName().indexOf(":") + 1, layerDTO.getName().length())
//						: layerDTO.getName();
				if (downloadShapfile(uri, layerName, Constant.DOWNLOAD_FOLDER)) {
					if (unzipShapeFileToFolder(Constant.DOWNLOAD_FOLDER + layerName + ".zip",
							Constant.DOWNLOAD_FOLDER + layerName)) {
						if (importShapeFileToDB(Constant.SHAPE_TO_DB_COMMAND,
								new String[] { Constant.DOWNLOAD_FOLDER + layerName + "\\" + layerName + ".shp",
										"public." + layerName, "|", Constant.IMPORT_COMMAND })) {
							totalSuccess = totalSuccess + 1;
						} else {
							totalFailed = totalFailed + 1;
						}

					}
				}
			}
		}
		Map<String, String> importDetails = new HashMap<String, String>();
		importDetails.put("SUCCESS", totalSuccess + "");
		importDetails.put("FAILED", totalFailed + "");
		return importDetails;

	}

	public boolean downloadShapfile(String url, String layer, String filePath) {
		boolean success = false;
		try {
			URL website = new URL(url + "?service=WFS&version=2.0.0&" + "request=GetFeature&typeName=" + layer
					+ "&outputFormat=SHAPE-ZIP&format_options=filename:" + layer + ".zip");
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(filePath + layer + ".zip");
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			success = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	public boolean importShapeFileToDB(String cmd, String[] args) {
		boolean success = false;
		try {
			boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
			ProcessBuilder builder = new ProcessBuilder();
			StringBuilder command = new StringBuilder(cmd);
			for (String string : args) {
				command.append(" ").append(string);
			}
			System.out.println(command.toString());
			if (isWindows) {
				builder.command("cmd.exe", "/c", command.toString());
			} else {
				// builder.command("sh", "-c", "ls");
			}
			builder.directory(new File(Constant.PGGIS_BASE_DIRECTORY));
			Process process = builder.start();

			StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
			Executors.newSingleThreadExecutor().submit(streamGobbler);
			int exitCode = process.waitFor();
			// System.out.println(exitCode);
			success = true;
			process.destroy();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	private boolean unzipShapeFileToFolder(String zipFilePath, String destDir) {
		File dir = new File(destDir);
		boolean success = false;
		// create output directory if it doesn't exist
		if (!dir.exists())
			dir.mkdirs();
		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());
				// create directories for sub directories in zip
				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();

		}
		return success;
	}

}

class StreamGobbler implements Runnable {
	private InputStream inputStream;
	private Consumer<String> consumer;

	public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
		this.inputStream = inputStream;
		this.consumer = consumer;
	}

	@Override
	public void run() {
		new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
	}

}