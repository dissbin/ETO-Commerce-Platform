package com.ETO.sellergoods.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.tools.jdi.VoidTypeImpl;

import entity.PageResult;
import entity.Result;

import com.ETO.mapper.TbBrandMapper;
import com.ETO.proj.TbBrand;
import com.ETO.proj.TbBrandExample;
import com.ETO.proj.TbBrandExample.Criteria;
import com.ETO.sellergoods.service.BrandService;
@Service
@Transactional
public class BrandServiceImpl implements BrandService {
	@Autowired
	private TbBrandMapper brandMapper;
	@Override
	public List<TbBrand> findAll() {
		return brandMapper.selectByExample(null);
	}
	@Override
	public PageResult findPage(int pageIndex, int pageSize) {
		PageHelper.startPage(pageIndex, pageSize);
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(TbBrand brand) {
		brandMapper.insert(brand);
	}
	@Override
	public TbBrand findOne(long id) {
		TbBrand brand = brandMapper.selectByPrimaryKey(id);
		return brand;
	}
	@Override
	public void updateBrand(TbBrand brand) {
		brandMapper.updateByPrimaryKey(brand);
	}
	@Override
	public void deleteBrand(long[] ids) {
		for(long id:ids) {
			brandMapper.deleteByPrimaryKey(id);
		}
	}
	@Override
	public PageResult findPage(TbBrand brand,int pageIndex, int pageSize) {
		PageHelper.startPage(pageIndex, pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria criteria = example.createCriteria();
		if(brand != null) {
			if(brand.getName() != null && brand.getName().length()>0)
				criteria.andNameLike("%"+ brand.getName() +"%");
			if(brand.getFirstChar() != null && brand.getFirstChar().length()>0)
				criteria.andFirstCharLike("%"+ brand.getFirstChar() +"%");
		}
		Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public List<Map> selectOptionList() {
		return brandMapper.selectOptionList();
	}
	
	
	
}
