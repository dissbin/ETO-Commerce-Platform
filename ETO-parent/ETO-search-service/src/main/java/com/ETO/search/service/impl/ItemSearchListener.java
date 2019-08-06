package com.ETO.search.service.impl;

import java.util.List;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ETO.proj.TbItem;
import com.ETO.search.service.ItemSearchService;
import com.alibaba.fastjson.JSON;

@Component
public class ItemSearchListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		System.out.println("已监听到消息");
		TextMessage textMessage = (TextMessage)message;
		try {
			String str = textMessage.getText();
			List<TbItem> itemList = JSON.parseArray(str, TbItem.class);
			itemSearchService.saveListToSolr(itemList);
			System.out.println("存入信息到Solr");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
