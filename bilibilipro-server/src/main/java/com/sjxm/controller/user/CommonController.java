package com.sjxm.controller.user;

import com.sjxm.constant.MessageConstant;
import com.sjxm.exception.VideoUploadFailedException;
import com.sjxm.result.Result;
import com.sjxm.service.VideoService;
import com.sjxm.utils.AliOssUtil;
import com.sjxm.utils.ProcessUtil;
import com.sjxm.vo.VideoInfoVO;
import com.sjxm.vo.VideoSrcPostVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Api(tags="通用接口")
@RestController
@RequestMapping("/user/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private VideoService videoService;

    @PostMapping("/uploadPic")
    @ApiOperation("图片文件上传")
    public Result<String> uploadPic(MultipartFile file){
        try{
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //原始文件名后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //新文件名
            String objectName = UUID.randomUUID()+extension;

            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(),objectName);
            return Result.success(filePath);
        }catch (IOException e){
            log.error("文件上传失败:{}",e);
        }
        return null;
    }

    @PostMapping("/uploadVideo")
    @ApiOperation("视频文件上传")
    public CompletableFuture<Result<VideoSrcPostVO>> uploadVideo(MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 原始文件名
                String originalFilename = file.getOriginalFilename();
                // 原始文件名后缀
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                // 新文件名
                String objectName = String.valueOf(UUID.randomUUID());

                // 获取上传的文件
                File tempFile = File.createTempFile("temp", null);
                file.transferTo(tempFile);
                VideoInfoVO videoInfo = videoService.getVideoInfo(tempFile);

                log.info(tempFile.getAbsolutePath());

                // hls切片
                String inputPath = tempFile.getAbsolutePath();
                String outputPath = "/Users/sijixiamu/Downloads/" + objectName;

                File outputDir = new File(outputPath);
                if (!outputDir.exists()) {
                    if (outputDir.mkdirs()) {
                        log.info("Output directory created: " + outputPath);
                        // 添加写入权限
                        outputDir.setWritable(true);
                    } else {
                        log.error("Failed to create output directory: " + outputPath);
                        // 处理目录创建失败的情况
                    }
                }

                List<String> command = buildFfmpegCommand(inputPath, outputPath, videoInfo.getHeight());
                ProcessUtil.execute(command);

                return Result.success(new VideoSrcPostVO(outputPath+"/master.m3u8", (int) videoInfo.getDuration()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new VideoUploadFailedException(MessageConstant.VIDEO_UPLOAD_FAILED);
            }
        }).completeOnTimeout(Result.success(new VideoSrcPostVO("", -1)), 5, TimeUnit.MINUTES);
    }


    private List<String> buildFfmpegCommand(String inputPath, String outputPath,int videoHeight) {

        List<String> scales = new ArrayList<>();
        scales.add("[0] scale=854:480[ed]");
        scales.add("[0] scale=854:480[ed],[0] scale=1280:720[hd]");
        scales.add("[0] scale=854:480[ed],[0] scale=1280:720[hd],[0] scale=1920:1080[fhd]");


        List<String> names = new ArrayList<>();
        names.add("v:0,a:0,name:480p");
        names.add("v:0,a:0,name:480p v:1,a:1,name:720p");
        names.add("v:0,a:0,name:480p v:1,a:1,name:720p v:2,a:2,name:1080p");

        List<String> command = new ArrayList<>();
        command.add("/opt/homebrew/opt/ffmpeg@4/bin/ffmpeg");
        command.add("-threads");
        command.add("0");
        command.add("-vsync");
        command.add("1");
        command.add("-i");
        command.add(inputPath);
        command.add("-lavfi");


        if(videoHeight == 720){
            command.add(scales.get(1));
        }
        else if(videoHeight == 1080){
            command.add(scales.get(2));
        }
        else{
            command.add(scales.get(0));
        }


        command.add("-c:v");
        command.add("libx264");
        command.add("-c:a");
        command.add("aac");


        if(videoHeight == 720){
            command.add("-b:v:0");
            command.add("1400k");
            command.add("-b:a:0");
            command.add("128k");
            command.add("-b:v:1");
            command.add("2800k");
            command.add("-b:a:1");
            command.add("128k");
        }
        else if(videoHeight == 1080){
            command.add("-b:v:0");
            command.add("1400k");
            command.add("-b:a:0");
            command.add("128k");
            command.add("-b:v:1");
            command.add("2800k");
            command.add("-b:a:1");
            command.add("128k");
            command.add("-b:v:2");
            command.add("5000k");
            command.add("-b:a:2");
            command.add("192k");
        }
        else{
            command.add("-b:v:0");
            command.add("1400k");
            command.add("-b:a:0");
            command.add("128k");
        }


        if(videoHeight == 720){
            command.add("-map");
            command.add("[ed]");
            command.add("-map");
            command.add("0:a");
            command.add("-map");
            command.add("[hd]");
            command.add("-map");
            command.add("0:a");
        }
        else if(videoHeight == 1080){
            command.add("-map");
            command.add("[ed]");
            command.add("-map");
            command.add("0:a");
            command.add("-map");
            command.add("[hd]");
            command.add("-map");
            command.add("0:a");
            command.add("-map");
            command.add("[fhd]");
            command.add("-map");
            command.add("0:a");
        }
        else{
            command.add("-map");
            command.add("[ed]");
            command.add("-map");
            command.add("0:a");
        }


        command.add("-f");
        command.add("hls");
        command.add("-var_stream_map");


        if(videoHeight == 720){
            command.add(names.get(1));
        }
        else if(videoHeight == 1080){
            command.add(names.get(2));
        }
        else{
            command.add(names.get(0));
        }


        command.add("-master_pl_name");
        command.add( "/master.m3u8");
        command.add("-hls_time");
        command.add("10");
        command.add("-hls_playlist_type");
        command.add("vod");
        command.add("-hls_list_size");
        command.add("0");
        command.add("-hls_segment_filename");
        command.add( outputPath + "/%v_%03d.ts");
        command.add( outputPath + "/%v.m3u8");


        return command;
    }



}
