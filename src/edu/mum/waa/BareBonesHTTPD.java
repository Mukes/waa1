package edu.mum.waa;

import java.io.*;
import java.net.*;
import java.util.*;

public class BareBonesHTTPD extends Thread {

	private static final int PortNumber = 8080;

	Socket connectedClient = null;

	public BareBonesHTTPD(Socket client) {
		connectedClient = client;
	}

	public void run() {

		try {
			System.out.println(connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

			BBHttpRequest httpRequest = getRequest(connectedClient.getInputStream());
			BBHttpResponse httpResponse = new BBHttpResponse();

			//processRequest(httpRequest, httpResponse);
			//processRequestFromFile(httpRequest, httpResponse);
			processDynamicRequest(httpRequest, httpResponse);

			sendResponse(httpResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void processRequest(BBHttpRequest httpRequest, BBHttpResponse httpResponse) {
		System.out.println("URI:"+httpRequest.getUri());
		StringBuilder response = new StringBuilder();
		response.append("<!DOCTYPE html>");
		response.append("<html>");
		response.append("<head>");
		response.append("<title>Almost an HTTP Server</title>");
		response.append("</head>");
		response.append("<body>");
		response.append("<h1>This is the HTTP Server</h1>");
		response.append("<h2>Your request was:</h2>\r\n");
		response.append("<h3>Request Line:</h3>\r\n");
		response.append(httpRequest.getStartLine());
		response.append("<br />");
		response.append("<h3> Header Fields: </h3>");
		for (String headerField : httpRequest.getFields()) {
			response.append(headerField.replace("<", "&lt;").replace("&", "&amp;"));
			response.append("<br />");
		}
		response.append("<h3> Payload: </h3>");
		for (String messageLine : httpRequest.getMessage()) {
			response.append(messageLine.replace("<", "&lt;").replace("&", "&amp;"));
			response.append("<br />");
		}
		response.append("</body>");
		response.append("</html>");

		httpResponse.setStatusCode(200);
		httpResponse.setMessage(response.toString());
	}


	private void processRequestFromFile(BBHttpRequest httpRequest, BBHttpResponse httpResponse) {
		System.out.println("URI:"+httpRequest.getUri());
		String response = null;
		try {
			response = FileHandler.readFile(httpRequest.getUri());
			response.replace("<", "&lt;").replace("&", "&amp;").replace(">", "&gt;");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (response==null){
			httpResponse.setStatusCode(500);
			StringBuilder responseTmp = new StringBuilder();
			responseTmp.append("HTTP Error 404").append("\n");
			responseTmp.append("Cannot Find File: ").append(httpRequest.getUri());
			response = responseTmp.toString();
		}else {
			httpResponse.setStatusCode(200);
		}
		httpResponse.setMessage(response.toString());
	}

	private void processDynamicRequest(BBHttpRequest httpRequest, BBHttpResponse httpResponse) {
		System.out.println("URI:"+httpRequest.getUri());
		String response = DynamicClass.dynamicResponse(httpRequest.getUri());
		if (response==null){
			try {
				response = FileHandler.readFile(httpRequest.getUri());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (response==null){
			httpResponse.setStatusCode(500);
			StringBuilder responseTmp = new StringBuilder();
			responseTmp.append("HTTP Error 404").append("\n");
			responseTmp.append("Cannot Find File: ").append(httpRequest.getUri());
			response = responseTmp.toString();
		}else {
			httpResponse.setStatusCode(200);
		}
		response.replaceAll("<", "&lt;").replaceAll("&", "&amp;").replaceAll(">", "&gt;");
		httpResponse.setMessage(response.toString());
	}

	private BBHttpRequest getRequest(InputStream inputStream) throws IOException {
		BBHttpRequest httpRequest = new BBHttpRequest();

		BufferedReader fromClient = new BufferedReader(new InputStreamReader(inputStream));

		String requestString = fromClient.readLine();
		String headerLine = requestString;

		System.out.println("The HTTP request is ....");
		System.out.println(requestString);

		// Header Line
		StringTokenizer tokenizer = new StringTokenizer(headerLine);
		httpRequest.setMethod(tokenizer.nextToken());
		httpRequest.setUri(tokenizer.nextToken());
		httpRequest.setHttpVersion(tokenizer.nextToken());

		// Header Fields and Body
		boolean readingBody = false;
		ArrayList<String> fields = new ArrayList<>();
		ArrayList<String> body = new ArrayList<>();

		while (fromClient.ready()) {

			requestString = fromClient.readLine();
			System.out.println(requestString);

			if (!requestString.isEmpty()) {
				if (readingBody) {
					body.add(requestString);
				} else {
					fields.add(requestString);
				}
			} else {
				readingBody = true;
			}
		}
		httpRequest.setFields(fields);
		httpRequest.setMessage(body);
		return httpRequest;
	}

	private void sendResponse(BBHttpResponse response) throws IOException {

		String statusLine = null;
		String contentLengthLine = "0";
		String contentTypeLine = "Not Available";
		if (response.getStatusCode() == 200) {
			statusLine = "HTTP/1.1 200 OK" + "\r\n";
			contentLengthLine = "Content-Length: " + response.getMessage().length() + "\r\n";
			contentTypeLine = "Content-Type: " + response.getContentType() + "\r\n";
		} else {
			statusLine = "HTTP/1.1 501 Not Implemented" + "\r\n";
		}

		String serverdetails = "Server: BareBones HTTPServer";


		DataOutputStream toClient = new DataOutputStream(connectedClient.getOutputStream());

		toClient.writeBytes(statusLine);
		toClient.writeBytes(serverdetails);
		toClient.writeBytes(contentTypeLine);
		toClient.writeBytes(contentLengthLine);
		toClient.writeBytes("Connection: close\r\n");
		toClient.writeBytes("\r\n");
		toClient.writeBytes(response.getMessage());

		toClient.close();
	}

	public static void main(String args[]) throws Exception {

		ServerSocket Server = new ServerSocket(PortNumber, 10, InetAddress.getByName("127.0.0.1"));
		System.out.println("Server Started on port " + PortNumber);

		LoadConfiguration.loader();
		while (true) {
			Socket connected = Server.accept();
			(new BareBonesHTTPD(connected)).start();
		}
	}
}
