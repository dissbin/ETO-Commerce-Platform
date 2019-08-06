package com.ETO.sellergoods.service.impl;
import java.util.List;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ETO.mapper.TbSpecificationOptionMapper;
import com.ETO.mapper.TbTypeTemplateMapper;
import com.ETO.proj.TbSpecificationOption;
import com.ETO.proj.TbSpecificationOptionExample;
import com.ETO.proj.TbTypeTemplate;
import com.ETO.proj.TbTypeTemplateExample;
import com.ETO.proj.TbTypeTemplateExample.Criteria;
import com.ETO.sellergoods.service.TypeTemplateService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}
	
	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		saveToRedis();
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> selectTypeList() {
			return typeTemplateMapper.selectTypeList();
		}

		@Autowired
		private TbSpecificationOptionMapper specificationOptionMapper;
		@Override
		public List<Map> findSpecList(long id) {
			TbTypeTemplate typeTemplete = typeTemplateMapper.selectByPrimaryKey(id);
			List<Map> list = JSON.parseArray(typeTemplete.getSpecIds(),Map.class);//转换字符串为集合
			for(Map map:list) {
				TbSpecificationOptionExample example = new TbSpecificationOptionExample();
				com.ETO.proj.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
				criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
				List<TbSpecificationOption> spectionOptions = specificationOptionMapper.selectByExample(example);
				map.put("options", spectionOptions);
			}
			return list;
		}


		private void saveToRedis() {
			List<TbTypeTemplate> list = findAll();//获取全部数据
			for(TbTypeTemplate typeTemplate:list) {
				//缓存品牌列表
				List<Map> brandIds= JSON.parseArray(typeTemplate.getBrandIds(),Map.class);//转换品牌数据为list
				redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandIds);
				System.out.println("品牌列表缓存完毕！");
				
				//缓存规格列表
				List<Map> specIds = findSpecList(typeTemplate.getId());
				redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specIds);
				System.out.println("规格列表缓存完毕！");
			}
		}

}
