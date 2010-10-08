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
		
		GpsData gpsData = new GpsData();
		gpsData.setLatitude(latitude);
		gpsData.setLongitude(longitude);
		gpsData.setPhoneName(phoneName);
		gpsData.setTime(time);
		
		PearlgrlzService pearlGrlzService = Context.getService(PearlgrlzService.class);
		pearlGrlzService.addGpsData(gpsData);
	}
	
}
