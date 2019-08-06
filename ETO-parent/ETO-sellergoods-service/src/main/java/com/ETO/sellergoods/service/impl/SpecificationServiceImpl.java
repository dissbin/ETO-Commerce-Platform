package com.ETO.sellergoods.service.impl;
import java.util.List;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ETO.mapper.TbSpecificationMapper;
import com.ETO.mapper.TbSpecificationOptionMapper;
import com.ETO.proj.TbSpecification;
import com.ETO.proj.TbSpecificationExample;
import com.ETO.proj.TbSpecificationExample.Criteria;
import com.ETO.proj.TbSpecificationOption;
import com.ETO.proj.TbSpecificationOptionExample;
import com.ETO.projgroup.Specification;
import com.ETO.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */


	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){
		//获取规格名称
		TbSpecification tbSpecification = specification.getTbSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);
		//删除原有规格名称对应ID的规格参数
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.ETO.proj.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example );
		//获取规格参数
		List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
		for(TbSpecificationOption tbSpecificationOption:tbSpecificationOptionList) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification = new Specification();
		//获取规格名称
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		specification.setTbSpecification(tbSpecification);

		//获取规格参数
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		com.ETO.proj.TbSpecificationOptionExample.Criteria createCriteria = example.createCriteria();
		createCriteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> list = specificationOptionMapper.selectByExample(example);
		specification.setTbSpecificationOptionList(list);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(long id:ids) {
			//删除规格名称
			specificationMapper.deleteByPrimaryKey(id);
			
			//删除规格参数
			TbSpecificationOptionExample example =new TbSpecificationOptionExample();
			com.ETO.proj.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> selectOptionList() {
			// TODO Auto-generated method stub
			return specificationMapper.selectOptionList();
		}

		@Override
		public void add(Specification specification) {
			//获取规格名称
			TbSpecification tbSpecification = specification.getTbSpecification();
			specificationMapper.insert(tbSpecification);
			//获取规格参数
			List<TbSpecificationOption> tbSpecificationOptionList = specification.getTbSpecificationOptionList();
			for(TbSpecificationOption tbSpecificationOption:tbSpecificationOptionList) {
				tbSpecificationOption.setSpecId(tbSpecification.getId());
				specificationOptionMapper.insert(tbSpecificationOption);
			}

		}
	
}
