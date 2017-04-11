package com.company.daniel.lind.karlberg;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServerOne {
    private static final Logger log = Logger.getLogger(ServerOne.class.getName());

    public static void main(String[] args) {
        log.info("Main initialized");
        setupLogging();

        try (ServerSocket serverSocket = new ServerSocket(61616)) {

            while (true) {
                log.info("Client socket created");
                File file = new File("/Users/daniellindkarlberg/IdeaProjects/FinaleServer/src/com/company/daniel/lind/karlberg/db.csv");
                Socket clientSocket = serverSocket.accept();

                new Thread(
                        new Runnable() {

                            public void run() {
                                System.out.println("client call");
                                log.fine("Client call");

                                try (InputStream inputStream = clientSocket.getInputStream();
                                     InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                                     OutputStream outputStream = clientSocket.getOutputStream();
                                     PrintWriter writer = new PrintWriter(outputStream);
                                     BufferedReader reader = new BufferedReader(inputStreamReader)) {

                                    Scanner scanner = null;
                                    for (String request = reader.readLine(); request != null; request = reader.readLine()) {
                                        System.out.println(request);
                                        log.fine("Request from client: " + request);
                                        if (request.equals("getall")) {
                                            scanner = new Scanner(new FileInputStream(file));
                                            log.fine("Request getall being processed");
                                            try {
                                                while (scanner.hasNextLine()) {
                                                    String line = scanner.nextLine().replace(",", " ");
                                                    writer.println(line);
                                                    writer.flush();
                                                }
                                            } catch (Exception e) {
                                                log.log(Level.SEVERE, "Database unavailable", e);
                                                System.out.println("Database unavailable");
                                            }
                                        }

                                        if (request.equals("exit")) {
                                            scanner.close();
                                            log.fine("Request to exit and close connection");
                                            break;
                                        }

                                    }

                                } catch (Exception e) {
                                    log.log(Level.SEVERE, "Error i/o", e);
                                }

                                log.info("All i/0 closed");
                            }

                        }
                ).start();

            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Socket error", e);
        }
    }


    private static void setupLogging() {
        log.info("Logging initialized");
        String loggingFilePath = "/Users/daniellindkarlberg/IdeaProjects/FinaleServer/log.properties";
        try (FileInputStream fileInputStream = new FileInputStream(loggingFilePath)) {
            LogManager.getLogManager().readConfiguration(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not load log properties.", e);
        }
    }


}
