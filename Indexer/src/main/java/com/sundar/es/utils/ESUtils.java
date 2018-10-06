/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sundar.es.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 *
 * @author sundar
 */
public class ESUtils {

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static void createByIndexTemplate(final String indexName, final Client client) {
        CreateIndexRequestBuilder indexRequestBuilder = null;

        indexRequestBuilder = client.admin().indices().prepareCreate(indexName + "-").setIndex(indexName);
        indexRequestBuilder.addAlias(new Alias(indexName+"_sarch"));

        indexRequestBuilder.execute().actionGet();
    }

    public static XContentBuilder convertJson(final String[] datas) throws IOException {
        return jsonBuilder().startObject()
                .field("ts", timeFormat.format(new Date(Long.parseLong(datas[1]))))
                .field("uid", datas[2])
                .field("id.orig_h", datas[3])
                .field("id.orig_p", Integer.valueOf(datas[4]))
                .field("id.resp_h", datas[5])
                .field("resp_p", Integer.valueOf(datas[6]))
                .field("trans_depth", Integer.valueOf(datas[7]))
                .field("method", datas[8])
                .field("host", datas[9])
                .field("uri", datas[10])
                .field("referrer", datas[11])
                .field("version", datas[12])
                .field("user_agent", datas[13])
                .field("request_body_len", Integer.valueOf(datas[14]))
                .field("response_body_len", Integer.valueOf(datas[15]))
                .field("status_code", Integer.valueOf(datas[16]))
                .field("status_msg", datas[17])
                .field("info_code", datas[18])
                .field("info_msg", datas[19])
                .field("tags", datas[20])
                .field("username", datas[21])
                .field("password", datas[22])
                .field("proxied", datas[23])
                .field("orig_fuids", datas[24])
                .field("orig_filenames", datas[25])
                .field("orig_mime_types", datas[26])
                .field("resp_fuids", datas[27])
                .field("resp_filenames", datas[28])
                .field("resp_mime_types", datas[29])
                .endObject();

    }

}
