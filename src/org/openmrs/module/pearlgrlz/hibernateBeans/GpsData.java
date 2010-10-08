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
}