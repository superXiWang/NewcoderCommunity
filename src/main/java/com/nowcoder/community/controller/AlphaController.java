package com.nowcoder.community.controller;

import com.nowcoder.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xi_wang
 * @create 2021-11-2021/11/29-20:45
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello Springboot!";
    }
    @RequestMapping("/getRequestParam1")
    public void getRequestParam1(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());
        System.out.println(request.getContextPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value = request.getParameter(name);
            System.out.println(name+":"+value);
        }
        request.getParameter("param1");

        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter printWriter = response.getWriter()){
            printWriter.write("<h1>牛客网</h1>");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/getRequestParam2")
    @ResponseBody
    public String getRequestParam2(String name, int age){
        System.out.println(name+":"+age);
        return name+":"+age;
    }

    @RequestMapping("/getRequestParam3")
    @ResponseBody
    public String getRequestParam3(
            @RequestParam(name = "name", required = false, defaultValue = "张三") String name,
            @RequestParam(name = "age", required = false, defaultValue = "1") int age){
        System.out.println(name+":"+age);
        return name+":"+age;
    }
    @RequestMapping("/getRequestParam4/{name}&{age}")
    @ResponseBody
    public String getRequestParam4(
            @PathVariable("name") String name,
            @PathVariable("age") int age){
        System.out.println(name+":"+age);
        return name+":"+age;
    }
    @RequestMapping(value = "/getRequestParam5",method = RequestMethod.POST)
    @ResponseBody
    public String getRequestParam5(String name, int age){
        System.out.println(name+":"+age);
        return name+":"+age;
    }

    @RequestMapping("/doResponse1")
    public ModelAndView doResponse1(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",1);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping("/doResponse2")
    public String doResponse2(Model model){
        model.addAttribute("name","李四");
        model.addAttribute("age",2);
        return "/demo/view";
    }

    @RequestMapping("/doResponse3")
    @ResponseBody
    public Map<String,String> doResponse3(){
        Map<String,String> map=new HashMap<String,String>();
        map.put("王五","3");
        map.put("赵六","4");
        return map;
    }

    @RequestMapping("/setCookie")
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        Cookie cookie=new Cookie("cookie", CommunityUtil.generateUUID());
        cookie.setPath("/community");
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping("/getCookie")
    @ResponseBody
    public String getCookie(@CookieValue("cookie") String cookie){
        return "get cookie:"+cookie;
    }

    @RequestMapping("/setSession")
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("msg","需要保存的信息");
        return "set session";
    }

    @RequestMapping("/getSession")
    @ResponseBody
    public String getSession(HttpSession session){
        return "get cookie:"+session.getAttribute("msg");
    }
}
