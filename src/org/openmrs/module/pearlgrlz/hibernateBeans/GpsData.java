package org.openmrs.module.pearlgrlz.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the dss_rule table
 * 
 * @author Tammy Dugan
 */
public class GpsData implements java.io.Serializable
{

	// Fields
	private Integer gpsDataId = null;
	private Double longitude = null;
	private Double latitude = null;
	private String phoneName = null;
	private Date time = null;
	private String address = null;
	private String locationProvider = null;
	private Double accuracy = null;
	private Double numSatellites = null;

	
    /**
     * @return the address
     */
    public String getAddress() {
    	return this.address;
    }

	
    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
    	this.address = address;
    }

	/**
	 * @return the ruleId
	 */
	public Integer getGpsDataId()
	{
		return this.gpsDataId;
	}

	/**
	 * @param gpsDataId the gpsDataId to set
	 */
	public void setGpsDataId(Integer gpsDataId)
	{
		this.gpsDataId = gpsDataId;
	}

	
    /**
     * @return the longitude
     */
    public Double getLongitude() {
    	return this.longitude;
    }

	
    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(Double longitude) {
    	this.longitude = longitude;
    }

	
    /**
     * @return the latitude
     */
    public Double getLatitude() {
    	return this.latitude;
    }

	
    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(Double latitude) {
    	this.latitude = latitude;
    }

	
    /**
     * @return the phoneName
     */
    public String getPhoneName() {
    	return this.phoneName;
    }

	
    /**
     * @param phoneName the phoneName to set
     */
    public void setPhoneName(String phoneName) {
    	this.phoneName = phoneName;
    }

	
    /**
     * @return the time
     */
    public Date getTime() {
    	return this.time;
    }

	
    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
    	this.time = time;
    }


	
    /**
     * @return the locationProvider
     */
    public String getLocationProvider() {
    	return this.locationProvider;
    }


	
    /**
     * @param locationProvider the locationProvider to set
     */
    public void setLocationProvider(String locationProvider) {
    	this.locationProvider = locationProvider;
    }


	
    /**
     * @return the accuracy
     */
    public Double getAccuracy() {
    	return this.accuracy;
    }


	
    /**
     * @param accuracy the accuracy to set
     */
    public void setAccuracy(Double accuracy) {
    	this.accuracy = accuracy;
    }


	
    /**
     * @return the numSatellites
     */
    public Double getNumSatellites() {
    	return this.numSatellites;
    }


	
    /**
     * @param numSatellites the numSatellites to set
     */
    public void setNumSatellites(Double numSatellites) {
    	this.numSatellites = numSatellites;
    }
    
}