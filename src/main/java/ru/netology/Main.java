package ru.netology;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    public static final String SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY";

    public static void main(String[] args) throws JsonProcessingException {
        byte[] bodyByte = client(SERVICE_URI);
        Nasa nasa = getListObject(bodyByte);
        byte[] obj = client(nasa.getUrl());
        List<String> url = Arrays.stream(nasa.getUrl().split("/")).collect(Collectors.toList());
        String nameFile = url.get(url.size() - 1);
        try {
            Files.write(Paths.get(nameFile), obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] client(String http) {
        byte[] bodyByte;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()) {
            HttpGet request = new HttpGet(http);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                bodyByte = response.getEntity().getContent().readAllBytes();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bodyByte;
    }

    public static Nasa getListObject(byte[] bodyByte) throws JsonProcessingException {
        String body = new String(bodyByte, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        Nasa nasa = mapper.readValue(body, new TypeReference<>() {
        });
        return nasa;
    }

}