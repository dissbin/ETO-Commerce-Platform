package com.ETO.page.service.impl;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import com.ETO.mapper.TbGoodsDescMapper;
import com.ETO.mapper.TbGoodsMapper;
import com.ETO.mapper.TbItemCatMapper;
import com.ETO.mapper.TbItemMapper;
import com.ETO.page.service.ItemPageService;
import com.ETO.proj.TbGoods;
import com.ETO.proj.TbGoodsDesc;
import com.ETO.proj.TbItem;
import com.ETO.proj.TbItemExample;
import com.ETO.proj.TbItemExample.Criteria;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

@Service
public class ItemPageServiceImpl implements ItemPageService{
	
	@Value("${directionLocation}")
	private String pageDir;
	
	@Autowired
	private FreeMarkerConfig freeMarkerConfig;
	
	@Autowired
	private TbGoodsMapper goodsMapper;
	
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public boolean getItemPage(Long goodsId) {
		//设置配置对象、模板路径、默认编码
		Configuration configuration = freeMarkerConfig.getConfiguration();
		//获取模板
		try {
			Template template = configuration.getTemplate("item.ftl");
			//获取数据模型
			Map dataModel = new HashMap<>();
			//1.获取商品模型
			TbGoods good = goodsMapper.selectByPrimaryKey(goodsId);
			
			//2.获取商品附属列表
			TbGoodsDesc goodDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
			if(good != null)
				dataModel.put("good", good);
			if(goodDesc != null)
				dataModel.put("goodDesc", goodDesc);
			
			//3.获取商品分类列表
			String category1Name = itemCatMapper.selectByPrimaryKey(good.getCategory1Id()).getName();
			String category2Name = itemCatMapper.selectByPrimaryKey(good.getCategory2Id()).getName();
			String category3Name = itemCatMapper.selectByPrimaryKey(good.getCategory3Id()).getName();
			dataModel.put("category1Name", category1Name);
			dataModel.put("category2Name", category2Name);
			dataModel.put("category3Name", category3Name);
			
			//4.获取SKU列表
			TbItemExample example = new TbItemExample();
			Criteria criteria = example.createCriteria();
			criteria.andGoodsIdEqualTo(goodsId);
			criteria.andStatusEqualTo("1");
			example.setOrderByClause("is_default desc");
			List<TbItem> itemList = itemMapper.selectByExample(example);
			dataModel.put("itemList", itemList);
			System.out.println("itemList:"+itemList);
			//输出模型
			try {
				Writer out = new FileWriter(new File(pageDir + goodsId + ".html"));
				template.process(dataModel, out);
				//关闭输出流
				out.close();
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} 
		
	
	}

	@Override
	public boolean deleteItemPage(Long[] goodsIds) {
		if(goodsIds == null)
			return false;
		try {
			for(Long goodsId:goodsIds) {
				new File(pageDir + goodsId + ".html").delete();
			}
			
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return false;
	}
	
}
