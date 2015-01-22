package Server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

public class FileServer implements IServe {

	StringBuffer response = new StringBuffer();
	byte[] ByteResponse;
	FileInputStream fis;
	File file;

	@Override
	public byte[] httpGet(String requestURI) throws HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		int last;
		String type;
		String returnType = null;
		StringBuffer body = new StringBuffer();

		
		try {

			requestURI = "web2/test" + requestURI;
			file = new File(requestURI);
			fis = new FileInputStream(file);

			last = file.getName().lastIndexOf(".");
			type = file.getName().substring(last + 1);

			returnType = null;

			if (type.equals("html")) {
				returnType = "text/html";
			} else if (type.equals("txt")) {
				returnType = "text/plain";
			} else if (type.equals("png")) {
				returnType = "image/png";
			} else if (type.equals("avi")) {
				returnType = "video/x-msvideo";
			} else if (type.equals("jpg")) {
				returnType = "image/jpeg";
			} else if (type.equals("ano")) {
				returnType = "application/x-annotator";
			} else if (type.equals("css")) {
				returnType = "text/css";
			} else if (type.equals("php")) {
				returnType = "text/html";
			}

			// if the the suffix is "php", then execute it as php file,otherwise
			// just read the file and move the date into "body"
			if (type.equals("php")) {

				body.append(PHP.execPHP(file.getAbsolutePath()));

			} else {

				int n;
				while ((n = fis.available()) > 0) {
					byte[] b = new byte[n];
					int result = fis.read(b);
					if (result == -1)
						break;
					body.append(new String(b));
				} // end while

			}

			// append the head information to the string "response"
			response.append("HTTP/1.0 200 OK\r\n");
			response.append("Date: " + new Date().toString().split(" ")[0]
					+ ", " + new Date().toGMTString() + "\r\n");
			response.append("Content-Length: "
					+ body.toString().trim().length() + "\r\n");
			response.append("Last-Modified: "
					+ new Date(file.lastModified()).toString().split(" ")[0]
					+ ", " + new Date(file.lastModified()).toGMTString()
					+ "\r\n");
			response.append("Content-Type: " + returnType + "\r\n\r\n");

			// append the body information to the string "response"
			response.append(body);

			// close the file inputstream
			fis.close();

			// transfer the string type of response to byte array type
			ByteResponse = (new String(response)).getBytes("US-ASCII");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			return "HTTP/1.0 404 Not Found".getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

		// return the byte array
		return ByteResponse;

	}

	@Override
	public byte[] httpGETconditional(String requestURI, Date ifModifiedSince)
			throws HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {

		requestURI = "web2/test" + requestURI;
		file = new File(requestURI);

		try {

			fis = new FileInputStream(file);

			// compare the last modified date from request with the date of the
			// file,
			// the file's date is before the request's date, return
			// "HTTP/1.0 304 Not Modified"
			// otherwise, return the httpGet information
			if (ifModifiedSince.after(new Date(file.lastModified())))
				ByteResponse = httpGet("/" + file.getName());
			else
				ByteResponse = (new String("HTTP/1.0 304 Not Modified"))
						.getBytes("US-ASCII");

		} catch (FileNotFoundException e) {
			return "HTTP/1.0 404 Not Found".getBytes();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ByteResponse;

	}

	@Override
	public byte[] httpHEAD(String requestURI) throws HTTPFileNotFoundException,
			HTTPRuntimeException, HTTPPermissionDeniedException {

		// split the date from httpGet with regular expression and select
		// the head date from the string list
		String httpget = new String(httpGet(requestURI));
		String[] temple = httpget.split("\r\n\r\n");

		// transfer the head information from string type to byte type
		try {
			ByteResponse = new String(temple[0]).getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ByteResponse;
	}

	@Override
	public byte[] httpPOST(String requestURI, byte[] postData)
			throws HTTPFileNotFoundException, HTTPRuntimeException,
			HTTPPermissionDeniedException {

		requestURI = "web2/test" + requestURI;
		file = new File(requestURI);

		try {

			if (!file.exists()) {

				// if the file does not exist, create the file
				file.createNewFile();
				response.append("HTTP/1.0 201 Created\r\n");

				// write the post date back into the new created file
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(postData);
				fos.close();

			}
			// if the file already existed, just return the get-information
			// from the file
			else {
				return httpGet("/" + file.getName());
			}

			ByteResponse = (new String(response.toString()))
					.getBytes("US-ASCII");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ByteResponse;
	}

}
