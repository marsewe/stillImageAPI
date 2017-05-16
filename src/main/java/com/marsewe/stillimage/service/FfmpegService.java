package com.marsewe.stillimage.service;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Calls to with ffmpeg.
 */
@Service
public class FfmpegService {


    private static final String ffmpegCommand = "ffmpeg -ss -i ";

    /**
     * @param videoFile
     * @param second
     * @param stillImagePath
     * @return true if extraction process finished succesfully
     */
    @Async
    public Future<Boolean> extractImage(final File videoFile, final long second, String stillImagePath) throws IOException {

        String line = generateCommandLine(videoFile, second, stillImagePath);
        CommandLine commandLine = CommandLine.parse(line);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        int exitValue = executor.execute(commandLine);

        Boolean succesfull = exitValue == 0;

        return new AsyncResult(succesfull);
    }


    protected String generateCommandLine(final File videoFile, final long second, final String stillImagePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("ffmpeg -ss 00:00:");
        sb.append(second); // TODO propper formatting for single digits
        sb.append(" ");
        sb.append(videoFile.getAbsolutePath());
        sb.append(" -vframes 1 -f image2 ");
        sb.append(stillImagePath);
        return sb.toString();
    }
}
