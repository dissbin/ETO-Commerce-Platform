package com.ETO.manager.controller;

import org.springframework.beans.factory.annotation.Value;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {
	
	@Value("${FILE_SERVEL_URL}")
	private String FILE_SERVEL_URL;
	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		System.out.println(file);
		String str = file.getOriginalFilename();//获取文件名
		String extName = str.substring(str.lastIndexOf(".") + 1);//获取后缀名
		try {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String tempUrl = client.uploadFile(file.getBytes(), extName);
			String fileId = FILE_SERVEL_URL + tempUrl;
			return new Result(true,fileId);//将地址藏在了返回信息message中
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"上传失败!");
		}
	}
}
