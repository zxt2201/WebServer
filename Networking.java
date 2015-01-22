package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Networking extends Thread{


	private Socket socket = null;

	public Networking(Socket socket) {
		this.socket = socket; // change the inside data field into what is input
	}

	public void run() {

		try {

			OutputStream out = socket.getOutputStream();
			BufferedInputStream in = new BufferedInputStream(
					socket.getInputStream());

			// create the arrray of byte to store the processed result of
			// inputstream
			// which will be sent back to client
			byte[] outputLine;

			// create the handler to process the inputstream
			Handler handler = new Handler();

			// create the array of byte to store the reading-result from
			// inputstream
			byte[] request = new byte[4000];

			in.read(request); // transfer the information from InputStream to
								// byte array "request"

			outputLine = handler.processRequest(request);// through the process
															// of handler, the
															// byte information
															// is stored into
															// "outputLine"

			out.write(outputLine);// the processed byte information is then
									// return back to outputStream

			// print out the request information and return information
			// separately
			System.out.print("\r\n\r\nRequest information: \n" + "\r\n"
					+ new String(request));
			System.out.print("\r\n\r\nReturn information: \n" + "\r\n"
					+ new String(outputLine));

			// Close the inputstream and outputstream as well as the socket
			out.close();
			in.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (HTTPFileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HTTPPermissionDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
