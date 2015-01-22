package Server;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Handler {

	private String URI = null;
	String body = null;
	Date time;
	
	@SuppressWarnings("deprecation")
	public byte[] processRequest(byte[] request)
			throws UnsupportedEncodingException, HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		// The byte array input is converted into string and stored into
		// "requestString"
		// To check the type of first word("GET" ,"POST", "HEAD") in request
		// string, the string
		// is separated with space and the result is saved into array, the first
		// element of
		// the array is picked out to match different method
		String requestString = new String(request);

		String[] ss1 = requestString.split(" ");

		String method = ss1[0];
		URI = ss1[1];

	

		Pattern p = Pattern.compile("\r\n\r\n(.*)");
		Matcher m = p.matcher(requestString);
		m.find();
		body = m.group(1);

		if (method.contains("If-Modified-Since")) {
			Pattern p2 = Pattern.compile("If-Modified-Since: (.*)");
			Matcher m2 = p2.matcher(requestString);
			m2.find();
			time = (Date) new java.util.Date(m2.group(1));		
			return GETconditional(URI, time);
		} 
		else if (method.equals("GET"))
			return GET(URI);
		else if (method.equals("POST"))
			return POST(URI, body);
		else if (method.equals("HEAD"))
			return HEAD(URI);
		else
			return (new String("HTTP/1.0 501 Not Implemented")
					.getBytes("US-ASCII"));

	}

	// The method of GET porcessing is defined,and return the
	// "HTTP/1.0 501 Not Implemented" with
	// byte type
	byte[] GET(String uri) throws UnsupportedEncodingException,
			HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {

		FileServer fileserver = new FileServer();

		
		return (fileserver.httpGet(uri));

	}

	byte[] GETconditional(String uri, Date date)
			throws UnsupportedEncodingException, HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		FileServer fileserver = new FileServer();

		return (fileserver.httpGETconditional(uri, date));

	}

	// The method of HEAD porcessing is defined,and return the
	// "HTTP/1.0 501 Not Implemented" with
	// byte type
	byte[] HEAD(String uri) throws UnsupportedEncodingException,
			HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {

		FileServer fileserver = new FileServer();

		return fileserver.httpHEAD(uri);

	}

	// The method of POST porcessing is defined,and return the
	// "HTTP/1.0 501 Not Implemented" with
	// byte type
	byte[] POST(String uri, String body) throws UnsupportedEncodingException,
			HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {
		FileServer fileserver = new FileServer();

		return fileserver.httpPOST(uri, body.getBytes("US-ASCII"));

	}

}
