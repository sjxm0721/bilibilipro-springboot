package com.sjxm.controller.user;

import com.alibaba.fastjson.JSON;
import com.sjxm.constant.MessageConstant;
import com.sjxm.context.BaseContext;
import com.sjxm.dto.DlBilibiliVideoDTO;
import com.sjxm.dto.MessageDTO;
import com.sjxm.exception.PicUploadFailedException;
import com.sjxm.result.Result;
import com.sjxm.service.VideoService;
import com.sjxm.utils.AliOssUtil;
import com.sjxm.utils.ProcessUtil;
import com.sjxm.vo.VideoInfoVO;
import com.sjxm.ws.WebSocketEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Api(tags="通用接口")
@RestController
@RequestMapping("/user/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @Autowired
    private VideoService videoService;

    @Autowired
    private WebSocketEndPoint webSocketEndPoint;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);


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
            throw new PicUploadFailedException(MessageConstant.PIC_UPLOAD_FAILED);
        }
    }


    @PostMapping("/uploadVideo")
    @ApiOperation("视频文件上传")
    public Result uploadVideo(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        Long uid = BaseContext.getUID();
                // 新文件名
                String objectName = String.valueOf(UUID.randomUUID());
                //本机ip地址
                String localIp = getLocalIp();

                // 获取上传的文件
                File tempFile = File.createTempFile("temp", null);
                file.transferTo(tempFile);

                VideoInfoVO videoInfo = videoService.getVideoInfo(tempFile);
                handleMp42M3U8(tempFile,objectName,uid,localIp,videoInfo);

                return Result.success();

    }

    private CompletableFuture handleMp42M3U8(File file,String objectName,Long uid,String localIp,VideoInfoVO videoInfo){

        log.info(file.getAbsolutePath());

        // hls切片
        String inputPath = file.getAbsolutePath();
        String outputPath = "/hls/" + objectName;

        File outputDir = new File(outputPath);
        if (!outputDir.exists()) {
            if (outputDir.mkdirs()) {
                log.info("Output directory created: " + outputPath);
            } else {
                log.error("Failed to create output directory: " + outputPath);
                // 处理目录创建失败的情况
            }
        }
        final ScheduledFuture<?>[] scheduledFuture = {null};
        List<String> command = buildFfmpegCommand(inputPath, outputPath, videoInfo.getHeight());
        return CompletableFuture.supplyAsync(()->{
            scheduledFuture[0] = scheduler.scheduleAtFixedRate(() -> {
                // 发送WebSocket消息，通知前端任务正在进行
                MessageDTO progressMessage = MessageDTO.builder()
                        .toUid(uid)
                        .isSystem('1')
                        .type('5')
                        .content("in_progress")
                        .build();
                try {
                    webSocketEndPoint.onMessage(JSON.toJSONString(progressMessage));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }, 0, 10, TimeUnit.SECONDS);
            ProcessUtil.execute(command);
            return null;
        },executorService).whenComplete((r,e)->{
            if (scheduledFuture[0] != null) {
                scheduledFuture[0].cancel(true);
            }
            if(e==null){
                MessageDTO messageDTO = MessageDTO.builder()
                        .toUid(uid)
                        .isSystem('1')
                        .type('5')
                        .content("success")
                        .videoUrl("http://"+localIp+'/'+objectName+"/master.m3u8")
                        .videoLastTime((int) videoInfo.getDuration())
                        .build();
                try {
                    webSocketEndPoint.onMessage(JSON.toJSONString(messageDTO));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).exceptionally(e->{
            e.printStackTrace();
            MessageDTO messageDTO = MessageDTO.builder()
                    .toUid(BaseContext.getUID())
                    .isSystem('1')
                    .type('5')
                    .content("error")
                    .build();
            try {
                webSocketEndPoint.onMessage(JSON.toJSONString(messageDTO));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        });
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
        command.add("ffmpeg");
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


    @PostMapping("/dlbilibili")
    @ApiOperation("下载b站视频")
    public Result dlBilibiliVideo(@RequestBody DlBilibiliVideoDTO dlBilibiliVideoDTO){
        Long uid = BaseContext.getUID();
        // 新文件名
        String objectName = String.valueOf(UUID.randomUUID());
        //本机ip地址
        String localIp = getLocalIp();
        log.info("{}",dlBilibiliVideoDTO);
        List<String> commands = buildBBDownCommand(dlBilibiliVideoDTO,objectName);
        CompletableFuture.supplyAsync(()->{
            ProcessUtil.execute(commands);
            return null;
        },executorService).whenComplete((r,e)->{
            if(e==null){
                log.info("获取视频信息成功");
                File fileTmp = new File("/hls/"+objectName+".mp4");
                VideoInfoVO videoInfo = videoService.getVideoInfo(fileTmp);
                handleMp42M3U8(fileTmp,objectName,uid,localIp,videoInfo).whenComplete((r1,e1)->{
                    List<String> commandDel = new ArrayList<>();
                    commandDel.add("rm");
                    commandDel.add("-rf");
                    commandDel.add("/hls/"+objectName+".mp4");
                    ProcessUtil.execute(commandDel);
                });
            }
        }).exceptionally(e->{
            e.printStackTrace();
            return null;
        });
        return Result.success();
    }


    private List<String> buildBBDownCommand(DlBilibiliVideoDTO dlBilibiliVideoDTO,String objectName) {

        List<String> command = new ArrayList<>();
        command.add("/www/server/bbdown/BBDown");
        command.add(dlBilibiliVideoDTO.getBid());
        if(!dlBilibiliVideoDTO.getSessData().isEmpty()){
            command.add("-c");
            command.add("SESSDATA="+dlBilibiliVideoDTO.getSessData());
        }
        if(dlBilibiliVideoDTO.getPnum()!=0){
            command.add("-p");
            command.add(dlBilibiliVideoDTO.getPnum().toString());
            command.add("--multi-file-pattern");
            command.add("/hls/"+objectName+".mp4");
        }
        else{
            command.add("--file-pattern");
            command.add("/hls/"+objectName+".mp4");
        }
        return command;
    }
    public String getLocalIp(){
        try {
            URL url = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String ipAddress = in.readLine();
            in.close();
            return ipAddress;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //关闭线程池
    @PreDestroy
    public void onDestroy() {
        // 在应用关闭时关闭线程池
        scheduler.shutdown();
        executorService.shutdown();
    }


}







