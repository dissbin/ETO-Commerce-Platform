package com.ETO.page.service.impl;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ETO.page.service.ItemPageService;

@Component
public class topicPageDeleteListener implements MessageListener{

	@Autowired
	private ItemPageService itemPageService;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage)message;
		try {
			Long[] ids=(Long[])objectMessage.getObject();
			itemPageService.deleteItemPage(ids);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
