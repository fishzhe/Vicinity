package com.vicinity.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;


public class XMlParser {

	private static String ns = null;
	
	public class Users{
		List<User> users;
		public Users(List<User> users){
			this.users = users;
		}
		public String toString(){
			return users.toString();
		}
	}
	
	public List<User> parse(InputStream in) throws XmlPullParserException, IOException{
		try{
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readUsers(parser);
		}
		finally{
			in.close();
		}
	}
	
	public List<User> readUsers(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "entitylist");
		List<User> users = new ArrayList<User>();
		User user = null; 
		while (parser.next()!= XmlPullParser.END_TAG){
			if(parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}
			String name = parser.getName();
			System.out.println("name " + name);
			if(name.equals("entity")){
				user = readUser(parser);
				System.out.println(user.toString());
				users.add(user);
			}else{
				skip(parser);
			}
			
			
		}
		return users;
	}
	public User readUser(XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "entity");
		User user = new User(); 			
		String lat = parser.getAttributeValue("", "lat");
		String lng = parser.getAttributeValue("", "lng");
		String name = parser.getAttributeValue("", "name");
		String distance = parser.getAttributeValue("", "distance");
		user.setLat(lat);
		user.setLng(lng);
		user.setName(name);
		user.setDistance(distance);
			
		String whatsup = readText(parser);
		user.setWhatsup(whatsup);
			
		
		return user;
	}
	public String readText(XmlPullParser parser) throws XmlPullParserException, IOException{
		String result= "";
		int next = parser.next();
		//System.out.println(next);
		
		if(next == XmlPullParser.TEXT){
			result += parser.getText();
			parser.nextTag();
		}
		//System.out.println(result);
		return result;
	}
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
	    if (parser.getEventType() != XmlPullParser.START_TAG) {
	        throw new IllegalStateException();
	    }
	    int depth = 1;
	    while (depth != 0) {
	        switch (parser.next()) {
	        case XmlPullParser.END_TAG:
	            depth--;
	            break;
	        case XmlPullParser.START_TAG:
	            depth++;
	            break;
	        }
	    }
	 }

}
