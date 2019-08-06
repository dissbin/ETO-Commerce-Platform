package com.ETO.search.service.impl;

import java.io.Serializable;

import java.util.List;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ETO.proj.TbItem;
import com.ETO.search.service.ItemSearchService;
import com.alibaba.fastjson.JSON;

@Component
public class ItemDeleteListener implements MessageListener {

	@Autowired
	private ItemSearchService itemSearchService;
	
	@Override
	public void onMessage(Message message) {
		System.out.println("----监听到消息----");
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			Long[] goodsids = (Long[])objectMessage.getObject();
			
			itemSearchService.deleteByGoodsId(goodsids);
			System.out.println("----已将Solr中相关信息删除！----");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
