package com.marsewe.stillimage.service;

import com.marsewe.stillimage.StillImageApiApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Generates still images
 */
@Service
public class StillImageService {


    private String downloadBaseUrl = "http://localhost:8080/download/";  //TODO: retrieve from system-properties.
    private static final Logger LOGGER = LoggerFactory.getLogger(StillImageService.class);



    @Autowired
    VideoManagerService videoManagerService;

    @Autowired
    FfmpegService ffmpegService;


    /**
     *  Provide a file to be downloaded.
     * @param filename
     * @return
     */
    public File downloadFile(String filename) {
        return new File(StillImageApiApp.STILL_IMAGE_PATH + filename);
    }



    /**
     * Generates a still image for the
     * @param videoId
     * @param position second to extract the image.
     * @param resolution
     * @param authorizationToken
     * @return download-url of the image
     */
    public URL generateStillImage(final String videoId,
                                  final long position,
                                  final String resolution, String authorizationToken) {


        File file = videoManagerService.downloadVideo(videoId, resolution, authorizationToken);
        String stillImagePath = generateStillImagePath(videoId, position, resolution);
        try {

            ffmpegService.extractImage(file, position, stillImagePath);
        } catch (IOException e) {
            LOGGER.error("could not extract image",e);
        }

        URL downloadUrl = null;
        try {
            downloadUrl = new URL(downloadBaseUrl + stillImagePath);
        } catch (MalformedURLException e) {
           LOGGER.error("Could not generate dowloadUrl", e);
        }

        return downloadUrl;
    }



    protected String generateStillImagePath(String videoId, long position, final String resolution) {
        StringBuilder sb = new StringBuilder();
        sb.append(videoId);
        sb.append("-");
        sb.append(position);
        sb.append("-");
        sb.append(resolution);
        sb.append("/stillimage.jpg");
        return sb.toString();
    }


    public boolean fileExists(String fileName) {
        return new File(StillImageApiApp.STILL_IMAGE_PATH + fileName).isFile();
    }
}
