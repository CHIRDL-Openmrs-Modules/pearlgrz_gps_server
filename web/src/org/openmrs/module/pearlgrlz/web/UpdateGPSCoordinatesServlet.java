package org.openmrs.module.pearlgrlz.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pearlgrlz.hibernateBeans.GpsData;
import org.openmrs.module.pearlgrlz.service.PearlgrlzService;

public class UpdateGPSCoordinatesServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1239820102033L;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Double longitude = null;
		Double latitude = null;
		Date time = null;
		Double accuracy = null;
		Double numSatellites = null;
		
		String longitudeString = request.getParameter("longitude");
		if(longitudeString != null){
			longitude = new Double(longitudeString);
		}
		
		String latitudeString = request.getParameter("latitude");
		if(latitudeString != null){
			latitude = new Double(latitudeString);
		}

		String phoneName = request.getParameter("phoneName");
		
		String timeString = request.getParameter("time");
		if(timeString != null){
			time = new Date(Long.parseLong(timeString));
		}
		
		String address = request.getParameter("address");
		String locationProvider = request.getParameter("locationProvider");
		String accuracyString = request.getParameter("accuracy");
		if(accuracyString != null){
			accuracy = new Double(accuracyString);
		}
		String numSatellitesString = request.getParameter("numSatellites");
		if(numSatellitesString != null){
			numSatellites = new Double(numSatellitesString);
		}
		
		String batteryLevel = request.getParameter("batteryLevel");
		
		GpsData gpsData = new GpsData();
		gpsData.setLatitude(latitude);
		gpsData.setLongitude(longitude);
		gpsData.setPhoneName(phoneName);
		gpsData.setTime(time);
		gpsData.setAddress(address);
		gpsData.setLocationProvider(locationProvider);
		gpsData.setAccuracy(accuracy);
		gpsData.setNumSatellites(numSatellites);
		gpsData.setBatteryLevel(batteryLevel);
		
		printGPSData(gpsData);
		
		PearlgrlzService pearlGrlzService = Context.getService(PearlgrlzService.class);
		pearlGrlzService.addGpsData(gpsData);
	}

	/**
     * Auto generated method comment
     * 
     * @param gpsData
     */
    private void printGPSData(GpsData gpsData) {
    	log.info("------------- GPS data received ---------------");
    	log.info("accuracy: "+gpsData.getAccuracy());
    	log.info("address: "+gpsData.getAddress());
    	log.info("gps data id: "+gpsData.getGpsDataId());
    	log.info("latitude: "+gpsData.getLatitude());
    	log.info("location provider: "+gpsData.getLocationProvider());
    	log.info("longitude: "+gpsData.getLongitude());
    	log.info("num satellites: "+gpsData.getNumSatellites());
    	log.info("phone name: "+gpsData.getPhoneName());
    	log.info("time: "+gpsData.getTime());
    	log.info("batteryLevel: "+gpsData.getBatteryLevel());
    	log.info("----------------------------------------------");
    }
	
}
