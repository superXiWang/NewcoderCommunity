package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.catalina.Host;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author xi_wang
 * @create 2021-12-2021/12/27-18:10
 */
@Controller
@RequestMapping("/user")
public class UserController {

    public static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /*
        响应 /user/setting.html
     */
    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /*
        响应 setting.html中表单提交图片的请求:/user/upload
     */
    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        // 判断图片是否为空
        if(headerImage==null){
            logger.error("上传的图片为空！");
            model.addAttribute("errorMsg","上传的图片为空！");
            return "/site/setting";
        }
        String filename=headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 判断图片格式
        if(StringUtils.isBlank(suffix)){
            logger.error("图片类型错误！");
            model.addAttribute("errorMsg","图片类型错误！");
            return "/site/setting";
        }

        // 将上传的图片存到服务器硬盘上的指定地址
        // 生成随机图片名
        filename= CommunityUtil.generateUUID()+suffix;
        // 存图片的目录
        File dest=new File(uploadPath+"/"+filename);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传图片失败！"+e.getMessage());
            throw new RuntimeException("上传图片失败！服务器发生异常！");
        }

        // 修改数据库中对应用户的headerUrl，固定headerUrl格式为 http://localhost:8080/community/user/header/xxx.png
        String headerUrl = domain+contextPath+"/user/header/"+filename;
        // 取已登录的用户
        User user = hostHolder.getValue();
        userService.updateHeaderUrl(user.getId(),headerUrl);

        return "redirect:/index";
    }

    /*
        响应headerUrl的请求:/user/header/xxx.png
     */
    @RequestMapping(value = "/header/{filename}",method = RequestMethod.GET)
    public void getHeaderImage(@PathVariable("filename") String filename, HttpServletResponse response){
        // 响应图片，需要知道文件绝对路径、设置响应内容类型、输出流输出
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/"+suffix);
        try (
                OutputStream outputStream = response.getOutputStream();
                FileInputStream fileInputStream = new FileInputStream(filename);
        ) {
            byte[] buffer=new byte[1024];
            int b=0;
            while ((b=fileInputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,b);
            }
            // System.out.println("-----------------UserController: getHeaderImage(): 正常输出图片");
        }catch (IOException e){
            logger.error("头像图片响应失败！"+e.getMessage());
        }
    }

    /*
        响应 setting.html表单中 提交密码的Post请求：/user/changePassword
     */
    @LoginRequired
    @RequestMapping(value = "/changePassword",method = RequestMethod.POST)
    public String changePassword(String oldPassword, String newPassword, Model model){
        // 检查原密码是否正确，若正确则将密码修改为新密码，并重定向到退出功能，强制用户重新登录
        User user = hostHolder.getValue();
        if(user!=null){
            String salt = user.getSalt();
            if(CommunityUtil.md5(oldPassword+salt).equals(user.getPassword())){
                userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword+salt));
                return "redirect:/logout";
            } else {
                // 若错误则返回到账号设置页面，给与相应提示
                model.addAttribute("oldPasswordErrorMsg","原密码输入错误！");
                return "/site/setting";
            }
        }
        return "/site/setting";
    }
}
