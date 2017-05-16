package com.marsewe.stillimage.service

import spock.lang.Specification

/**
 * Created by martin on 16.05.17.
 */
class VideoManagerServiceSpec extends Specification {

    private static final String USER_NAME = "bla123@boatmail.us";
    private static final String PASSWORD = "";



    VideoManagerService videoManagerService = new VideoManagerService()


    def "authenticate returns token"() {

        when: "VideoManagerPro URL is called"
        String result = videoManagerService.authenticate(USER_NAME, PASSWORD)

        then: "authentication token returned"
        assert result != null
        assert result instanceof String
    }




    def "downloadVideo returns file"() {
        String testVideoId = "1nJJLay88vUj5c43EjdK29"

        given: "authenticated used"
        String accessToken = videoManagerService.authenticate(USER_NAME, PASSWORD);

        when: "download-request on video-manager-api performed"
        File videoFile = videoManagerService.downloadVideo(testVideoId, "720p", accessToken);

        then: "video-file returned"
        assert videoFile.isFile()
    }


}
