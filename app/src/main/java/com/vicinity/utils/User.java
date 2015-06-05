package com.vicinity.utils;

public class User {

	private String name;
	private String address;
	private String lat;
	private String lng;
	private String distance;
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	private String whatsup;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getWhatsup() {
		return whatsup;
	}
	public void setWhatsup(String whatsup) {
		this.whatsup = whatsup;
	}
	public String toString(){
		String str = "*******************************************\n" + 
				name + " says:" + whatsup + "\nat(" + lat + ", " + lng +  ")" +
				 "\ndistance: " + distance
				 +"\n*******************************************";
		return str;
	}
}
