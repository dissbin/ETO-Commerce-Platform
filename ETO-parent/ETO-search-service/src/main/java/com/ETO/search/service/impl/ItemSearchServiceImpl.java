package com.ETO.search.service.impl;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.MacSpi;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import com.ETO.proj.TbItem;
import com.ETO.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Service;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate redisTemplate;


	private Map itemSearchServiceImpl(Map mapSearch) {
		Map<String,Object> map = new HashMap<>();
		HighlightQuery query=new SimpleHighlightQuery();
		HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
		highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀 
		highlightOptions.setSimplePostfix("</em>");//高亮后缀
		query.setHighlightOptions(highlightOptions);//设置高亮选项
		//按照关键字查询
		Criteria criteria=new Criteria("item_keywords").is(mapSearch.get("keywords"));
		query.addCriteria(criteria);
		//设置分页
		Integer pageIndex = (Integer) mapSearch.get("pageIndex");
		Integer pageSize = (Integer)mapSearch.get("pageSize");
		if(pageIndex == null) {
			pageIndex = 1;
		}
		if(pageSize == null) {
			pageSize = 20;
		}
		query.setOffset(pageIndex);//前端的页码
		query.setRows(pageSize);//前端一页的数目
		//设置排序
		String sortWay = (String) mapSearch.get("sortWay");
		String sortField = (String) mapSearch.get("sortField");

		if((!sortWay.equals("")) && (!sortField.equals(""))) {
			System.out.println("2sortWay:"+sortWay);
			System.out.println("2sortField:"+sortField);
			Sort sort = new Sort(Sort.Direction.ASC, "item_"+sortField);//默认排序
			if(sortWay.equals("DESC")) {
				sort = new Sort(Sort.Direction.DESC, "item_"+sortField);//降序排序
			}
			query.addSort(sort);
		}
		//商品类别过滤
		if(!mapSearch.get("category").equals("")) {   //如果用户选择了类别
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_category").is(mapSearch.get("category"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//商品品牌过滤
		if(!mapSearch.get("brand").equals("")) {   //如果用户选择了类别
			FilterQuery filterQuery = new SimpleFilterQuery();
			Criteria filterCriteria = new Criteria("item_brand").is(mapSearch.get("brand"));
			filterQuery.addCriteria(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//商品规格过滤
		if(mapSearch.get("spec") != null) {
			Map<String, String> specMap = (Map<String, String>) mapSearch.get("spec");
			for(String key:specMap.keySet()) {
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
		}
		//价格过滤
		if(!mapSearch.get("priceRange").equals("")) {//选择了价格区间
			String[] split = mapSearch.get("priceRange").toString().split("-");
			if(!split[0].equals("0")) {//价格为0-500区间时不设下限
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(split[0]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery );
			}
			if(!split[1].equals("*")) {//价格为3000以上不设上限
				FilterQuery filterQuery = new SimpleFilterQuery();
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(split[1]);
				filterQuery.addCriteria(filterCriteria);
				query.addFilterQuery(filterQuery );
			}	
		}
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
		System.out.println("高亮页:"+page);
		for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
			TbItem item = h.getEntity();//获取原实体类	
			System.out.println("高亮Highlights:"+h.getHighlights());
			if(h.getHighlights().size()>0 && h.getHighlights().get(0).getSnipplets().size()>0){
				item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
			}			
		}
		//分页

		
		map.put("rows", page.getContent());//返回一页的数据
		map.put("totalPages", page.getTotalPages());//返回总页数
		map.put("totalElements", page.getTotalElements());//返回总记录数
		
		return map;
	}
	
	@Override
	public Map search(Map mapSearch) {
		Map map = new HashMap<>();
		String keywords = (String) mapSearch.get("keywords");
		mapSearch.put("keywords", keywords.replace(" ", ""));
		System.out.println("keywords:"+keywords);
		//1.搜索结果
		Map mapSearchResult = itemSearchServiceImpl(mapSearch);
		map.putAll(mapSearchResult);
		//2.分组查询结果
		List<String> categoryList =  searchCategoryList(mapSearch);
		map.put("categoryList", categoryList);
		//3.品牌列表和规格列表
		String category = (String) mapSearch.get("category");
		if(!category.equals("")) {//选中分类加载数据
			Map brandAndSpecMap = searchBrandAndSpecList(category);
			map.putAll(brandAndSpecMap);
		}
		else {
			if(categoryList.size()>0) {//没选分类，第一次加载
				Map brandAndSpecMap = searchBrandAndSpecList(categoryList.get(0));
				map.putAll(brandAndSpecMap);
			}
		}
		
		return map;
	}
	
	private List<String> searchCategoryList(Map mapSearch){
		
		List<String> list = new ArrayList<>();
		Query query = new SimpleQuery("*:*");
		//关键字查询
		Criteria criteria = new Criteria("item_keywords").is(mapSearch.get("keywords"));
		query.addCriteria(criteria);
		

	
		//分组设置
		GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");//可能会有多个分组
		query.setGroupOptions(groupOptions);
		//分组页
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		//分组对象
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");//取得category分组
		
		//分组入口集合
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();

		//分组入口
		List<GroupEntry<TbItem>> entrys = groupEntries.getContent();

		for(GroupEntry<TbItem> entry:entrys) {
			String str = entry.getGroupValue();//单个分组名称

			list.add(str);
		}
		return list;
	}

	
	
	private Map searchBrandAndSpecList(String specName) {
		Map<String,Object> map = new HashMap<>();
		//获取模板ID
		Long specId = (Long) redisTemplate.boundHashOps("itemCat").get(specName);
		if (specId!=null) {
			map.put("specId", specId);
			//获取品牌列表
			List<Map> brandList  = (List<Map>) redisTemplate.boundHashOps("brandList").get(specId);
			map.put("brandList", brandList);
			//获取规格
			List<Map> specList  = (List<Map>) redisTemplate.boundHashOps("specList").get(specId);
			map.put("specList", specList);
		}
		return map;	
	}
	
	//将新添加商品存进solr中
	@Override
	public void saveListToSolr(List<TbItem> itemList) {
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
	}

	//同步删除solr数据库
	@Override
	public void deleteByGoodsId(Long[] ids) {
		Query query = new SimpleQuery("*:*");
		Criteria criteria = new Criteria("item_goodsid").in(ids);
		query.addCriteria(criteria );
		solrTemplate.delete(query );
		solrTemplate.commit();
	}
	
}
