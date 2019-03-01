package org.academiadecodigo.bootcamp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Mainserver {

	private int portNumber;
	private BufferedReader inputFromClient;
	private DataOutputStream outputfromClient;
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private FileInputStream textFiles;
	private File file;


	public Mainserver(int portNumber) {

		this.portNumber = portNumber;

	}

	public static void main(String[] args) throws IOException {
		final int PORT_NUMBER = 7070;

		Mainserver webServer = new Mainserver(PORT_NUMBER);
		webServer.serverCreation(PORT_NUMBER);


		while (true) {
			webServer.openConnections();
			webServer.receiveAndSendInformation();
			webServer.closeConnections();
		}


	}

	private void fileStream(File file) {
		try {
			textFiles = new FileInputStream(file);

			byte[] buffer = new byte[1024];
			int numBytes;

			while ((numBytes = textFiles.read(buffer)) != -1) {
				outputfromClient.write(buffer, 0, numBytes);
			}

		} catch (FileNotFoundException e) {
			System.out.println("file not found " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Nop error " + e.getMessage());
		}


	}

	private void serverCreation(int portNumber) throws IOException {
		serverSocket = new ServerSocket(portNumber);
	}

	private void openConnections() throws IOException {

		clientSocket = serverSocket.accept();
		System.out.println("Connection established");
		outputfromClient = new DataOutputStream((clientSocket.getOutputStream()));
		inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	private void closeConnections() throws IOException {
		outputfromClient.close();
		inputFromClient.close();
		clientSocket.close();
		System.out.println("Connection closed");

	}

	public String header(File file){

		String contentStatusCode = getStatusCode(file);
		String contentType = getContentType(file);
		long contentLength = file.length();

		String header = "HTTP/1.0 " + contentStatusCode + "\r\n Content-Type: " + contentType + " charset=UTF-8\r\n Content-Length: " + contentLength + "\r\n\r\n";

		return header;

	}

	private void receiveAndSendInformation() throws IOException {

		String header = inputFromClient.readLine();
		System.out.println("Client requested " + header);
		String filePath = header.split(" ")[1];


		file = new File("www" + filePath);
		file.exists();
		String extention = filePath.split("\\.")[1];

		switch (extention) {

			case "html":
				outputfromClient.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: text/html; charset=UTF-8\r\n" +
						"Content-Length: " + file.length() + "\r\n\r\n");
				break;

			case "jpg":
				outputfromClient.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: image/jpg \r\n" +
						"Content-Length: " + file.length() + "\r\n\r\n");
				break;

			case "mp4":
				outputfromClient.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: video/mp4 \r\n" +
						"Content-Length: " + file.length() + "\r\n\r\n");
				break;

			case "gif":
				outputfromClient.writeBytes("HTTP/1.0 200 Document Follows\r\n" +
						"Content-Type: image/gif \r\n" +
						"Content-Length: " + file.length() + "\r\n\r\n");
				break;


		}

		fileStream(file);
		System.out.println(file + " delivered");
	}

}
