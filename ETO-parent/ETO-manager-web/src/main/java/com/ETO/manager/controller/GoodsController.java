package com.ETO.manager.controller;
import java.util.List;


import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.zookeeper.server.SessionTracker.Session;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.ETO.proj.TbGoods;
import com.ETO.proj.TbItem;
import com.ETO.projgroup.Goods;
import com.ETO.sellergoods.service.GoodsService;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination queueSolrDestination;
	
	@Autowired
	private Destination topicPageDestination;
	
	@Autowired
	private Destination topicPageDeleteDestination;
	//@Reference(timeout=100000)
	//private itemSearchService searchService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@Autowired
	private Destination queueSolrDeleteDestination;
	
	
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//同步删除solr数据库	
			//searchService.deleteByGoodsId(ids);
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(javax.jms.Session session) throws JMSException {
					
					return session.createObjectMessage(ids);
				}
			});
				//同步删除静态网页
				jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				
				@Override
				public Message createMessage(javax.jms.Session session) throws JMSException {
					
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}
	
	
	
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long [] ids,String status) {
		try {
			goodsService.updateStatus(ids, status);
			if("1".equals(status)) {//若审核通过
				//获取新添加商品
				List<TbItem> itemList = goodsService.findNewGoods(ids, status);
			
				if(itemList.size() > 0) {
					//將itemList转成json字符串
					final String itemListString = JSON.toJSONString(itemList);
					//将新添加商品存进solr中
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(javax.jms.Session session) throws JMSException {
							return session.createTextMessage(itemListString);
						}
					});
				}
				else
					System.out.println("没有明细数据!");
				//生成商品sku
				for(final Long id:ids) {
					//itemPageService.getItemPage(id);
					jmsTemplate.send(topicPageDestination, new MessageCreator() {
						
						@Override
						public Message createMessage(javax.jms.Session session) throws JMSException {
							return session.createTextMessage(id.toString());
						}
					});
				}
				
			}

			return new Result(true,"更新成功!");
		} catch (Exception e) {
			
			e.printStackTrace();
			return new Result(false,"更新失败!");
		}
	}
	
	
//	@RequestMapping("/outputHtml")
//	public void outputHtml(Long goodId) {
//		itemPageService.getItemPage(goodId);
//	}
//	

}
