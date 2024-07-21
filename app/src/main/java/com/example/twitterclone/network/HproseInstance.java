package com.example.twitterclone.network;

import hprose.client.HproseClient;

import java.io.IOException;
import java.net.URISyntaxException;

//public class HproseInstance {
//    private static final String BASE_URL = "http://localhost:8081/webapi/";
//
//    private static volatile HproseService client; // Use volatile for thread safety
//
//    public static HproseService getClient() {
//        if (client == null) {
//            synchronized (HproseInstance.class) {
//                if (client == null) {
//                    try {
//                        HproseClient hproseClient = HproseClient.create(BASE_URL);
//                        client = hproseClient.useService(HproseService.class, "Leither");
//                        System.out.print(client);
//                    } catch (IOException e) {
//                        System.err.println("Error creating Hprose client: " + e.getMessage());
//                        // Handle the exception further as needed
//                    }catch (URISyntaxException e) {
//                        System.err.println("Invalid URL: " + e.getMessage());
//                        // Handle the exception further as needed
//                    }
//                }
//            }
//        }
//        return client;
//    }
//}