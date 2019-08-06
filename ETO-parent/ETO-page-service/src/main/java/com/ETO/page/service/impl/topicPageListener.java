package com.ETO.page.service.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ETO.page.service.ItemPageService;

@Component
public class topicPageListener implements MessageListener{

	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage = (TextMessage)message;
		try {
			String idString = textMessage.getText();
			System.out.println("已监听到消息:"+idString);
			itemPageService.getItemPage(Long.parseLong(idString));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
