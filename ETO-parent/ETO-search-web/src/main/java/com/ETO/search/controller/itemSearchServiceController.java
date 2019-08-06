package com.ETO.search.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ETO.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/itemSearch")
public class itemSearchServiceController {
	
	@Reference(timeout=40000)
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/search")
	public Map<String,Object> search(@RequestBody Map mapSearch) {
		return itemSearchService.search(mapSearch);
	}
}
