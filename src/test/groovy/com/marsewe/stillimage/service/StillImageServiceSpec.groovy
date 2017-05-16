package com.marsewe.stillimage.service

import spock.lang.Specification

/**
 * Created by martin on 16.05.17.
 */
class StillImageServiceSpec extends Specification {


    def "generateStillImage generates image"() {
        given: "authenticated user"
        StillImageService stillImageService = new StillImageService()
        stillImageService.videoManagerService = new VideoManagerService()
        stillImageService.ffmpegService = new FfmpegService()
        String testVideoId = "1nJJLay88vUj5c43EjdK29"
        String userName = "bla123@boatmail.us"
        String password = ""

        String accessToken = stillImageService.videoManagerService.authenticate(userName, password);


        when: "generateStillImage is called"
        URL result = stillImageService.generateStillImage(testVideoId, 10, "test", accessToken);

        then: "download-url is returned"
        assert result != null
    }
}
