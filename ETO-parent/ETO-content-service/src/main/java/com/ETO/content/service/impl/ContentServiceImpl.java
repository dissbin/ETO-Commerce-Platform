package com.ETO.content.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ETO.mapper.TbContentMapper;
import com.ETO.proj.TbContent;
import com.ETO.proj.TbContentExample;
import com.ETO.proj.TbContentExample.Criteria;
import com.ETO.content.service.ContentService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);		
		//清除缓存
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		//查询原来的分组ID
		Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		//清除原分组的缓存
		redisTemplate.boundHashOps("content").delete(categoryId);
		//更新广告
		contentMapper.updateByPrimaryKey(content);
		//如果更新后的类别ID发生变化如果不删除新ID的缓存，那么下次查找的就是旧值
		if(categoryId.longValue() != content.getCategoryId().longValue())
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){		
			contentMapper.deleteByPrimaryKey(id);
			TbContent content = contentMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
		


	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		
		List<TbContent> list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
		if(list == null) {
			System.out.println("从数据库中取值");
			TbContentExample example = new TbContentExample();
			Criteria criteria = example.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");//上架广告
			example.setOrderByClause("sort_order");//排序
			list = contentMapper.selectByExample(example);
			redisTemplate.boundHashOps("content").put(categoryId, list);
		}else 
			System.out.println("从缓存中取值");
		
		
		return list;
	}
	
	
}
