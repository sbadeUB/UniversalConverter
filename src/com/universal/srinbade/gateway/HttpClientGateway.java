package com.universal.srinbade.gateway;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HttpClientGateway {
    private final String USER_AGENT = "Mozilla/5.0";

    // HTTP GET request
    public Optional<String> sendGet(final String httpUrl) {
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet(httpUrl);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);

        try {
            final HttpResponse response = client.execute(request);
            System.out.println("[DEBUG] Response Code : " + response.getStatusLine().getStatusCode());

            // process response and store it as string.
            try (final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                final StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                return Optional.of(result.toString());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // HTTP POST request
    public Optional<String> sendPost(final String httpUrl, final Map<String, String> postParams) {
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(httpUrl);

        try {
            // add request header
            post.setHeader("User-Agent", USER_AGENT);

            // add request parameters
            final List<NameValuePair> urlParams = new ArrayList<>();
            postParams.forEach((key, value) -> {
                urlParams.add(new BasicNameValuePair(key, value));
            });
            post.setEntity(new UrlEncodedFormEntity(urlParams));

            final HttpResponse response = client.execute(post);
            System.out.println("[DEBUG] Response Code : " + response.getStatusLine().getStatusCode());

            // process response and store it as string.
            try (final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                final StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                return Optional.of(result.toString());
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}