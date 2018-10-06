/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sundar.es.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;

import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 *
 * @author sundar
 */
public class ESConnection {

    private static Logger log = Logger.getLogger(ESConnection.class);

    public static Client getClient(String serverIP, int serverPort, String clusterName) throws InterruptedException, UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "120s").build();
        log.info(serverIP + "/" + serverPort + ": " + clusterName);
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(serverIP), serverPort));

        return client;
    }

}
