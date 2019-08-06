package com.ETO.portal.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ETO.content.service.ContentService;
import com.ETO.proj.TbContent;
import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/content")
public class contentController {
	@Reference(timeout=40000)
	private ContentService contentService;
	
	@RequestMapping("/findByCategoryId")
	public List<TbContent> findByCategoryId(Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
}
