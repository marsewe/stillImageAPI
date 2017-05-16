package com.marsewe.stillimage.service;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Communication to the VideoManager.
 */
@Service
public class VideoManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoManagerService.class);


    private static final String VIDEO_MANAGER_URL = "https://api.video-cdn.net/v1";

    private static final String LOGIN_URL = "/vms/auth/login";
    private static final String REFRESH_URL = "/vms/auth/refresh/";
    private static final String DOWNLOAD_URL = "/vms/{videoManagerId}/videos/{videoId}/download-urls";


    private String videoManagerId; //TODO make this work for different video-managers


    /**
     * Authenticates the user
     * TODO: error-handling, string-constants
     *
     * @return token to be used for further request.
     */
    public String authenticate(String username, String password) {

        RestTemplate loginTemplate = restTemplateBuilder().build();
        Map<String, String> loginParameters = new HashMap<>();
        loginParameters.put("username", username);
        loginParameters.put("password", password);
        Map loginResult = loginTemplate.postForObject(LOGIN_URL, loginParameters, Map.class);

        LOGGER.debug("loginResult\n" + loginResult);


        Map videoManager = (Map) ((ArrayList) loginResult.get("videoManagerList")).get(0);
        this.videoManagerId = videoManager.get("id").toString();

//        RestTemplate refreshTemplate = restTemplateBuilder().build()//
//        Map refreshParameters = new HashMap<>();
//        refreshParameters.put("refreshToken", loginResult.get("refreshToken"));
//
//        Map refreshResult = refreshTemplate.postForObject(REFRESH_URL + "/" + videoManagerId,
//                refreshParameters, Map.class);
//
//
//        LOGGER.debug("refreshResult\n" + refreshResult);

        return loginResult.get("accessToken").toString();

    }


    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
                .rootUri(VIDEO_MANAGER_URL);
    }


    /**
     * Download a video from videoManager
     *
     * @param videoId
     * @param quality
     * @param accessToken
     * @return TODO: take care of resoultion
     */
    public File downloadVideo(final String videoId, final String quality, final String accessToken) {
        RestTemplate restTemplate = restTemplateBuilder().build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);


        Map<String, String> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("videoManagerId", videoManagerId);
        HttpEntity<String> entity = new HttpEntity<String>("", headers);

        String videoDownloadUrl = "";
        try {
            ResponseEntity<List> response = restTemplate.exchange(DOWNLOAD_URL, HttpMethod.GET, entity, List.class, params);
            List responseBody = response.getBody();
//            Object DOWNLOAD_URL = responseBody.
//                    stream().filter((Map) map -> map.get("quality".equals(quality)).
//                    collect(map.get("url"));
            // TODO: filter for correct downloadurl

            videoDownloadUrl = ((Map) responseBody.get(0)).get("url").toString();

        } catch (final HttpClientErrorException e) {
            LOGGER.error("HttpStatus: " + e.getStatusCode());
            LOGGER.error(e.getResponseBodyAsString());
        }


        URI uri = null;
        try {
            uri = new URI(videoDownloadUrl);
        } catch (URISyntaxException e) {
            LOGGER.error("invalid DOWNLOAD_URL " + videoDownloadUrl, e);
        }

        File downloadedVideo = new File("/tmp" + uri.getPath());
        try {
            FileUtils.copyURLToFile(uri.toURL(), downloadedVideo);
        } catch (IOException e) {
            LOGGER.error("could not download file " + videoDownloadUrl, e);
        }

        LOGGER.debug("Downloaded video to " + downloadedVideo.getAbsolutePath());

        return downloadedVideo;

    }
}
