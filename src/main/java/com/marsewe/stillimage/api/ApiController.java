package com.marsewe.stillimage.api;

import com.marsewe.stillimage.service.StillImageService;
import com.marsewe.stillimage.service.VideoManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Map;

/**
 * Created by martin on 16.05.17.
 */
@RestController
public class ApiController {

    private static final String USER_NAME = "bla123@boatmail.us";
    private static final String PASSWORD = "TODO";



    @Autowired
    private VideoManagerService videoManagerService;

    @Autowired
    private StillImageService stillImageService;


    /**
     * Authenticate the user
     * TODO: customized username and password, encryption/https, error-handling
     * @return authentication token for further calls
     */
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public String authenticate() {

        return videoManagerService.authenticate(USER_NAME, PASSWORD);

    }


    /**
     * Trigger the generation of a still image.
     * @param videoId
     * @param position
     * @param resolution
     * @return download url of the still image.
     */
    @RequestMapping(value = "/stillImage", method = RequestMethod.PUT)
    public String generateStillImage(@RequestParam("videoId") final String videoId,
                                  @RequestParam("position") final long position,
                                  @RequestParam("resolution") final String resolution,
                                     @RequestHeader("authorization") final String authorizationToken) {

        return stillImageService.generateStillImage(videoId, position, resolution, authorizationToken).toString();
    }


    /**
     * Download a still image
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/download/{file_name}", method = RequestMethod.GET)
    public ResponseEntity downloadStillImage(@PathVariable("file_name") String fileName) {
        if (stillImageService.fileExists(fileName)) {
            FileSystemResource fileSystemResource = new FileSystemResource(stillImageService.downloadFile(fileName));
            return new ResponseEntity(fileSystemResource, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }
}
