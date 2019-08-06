package com.ETO.sellergoods.service;
/*
 * 品牌接口
 * @author DongZhibin
 * */

import java.util.List;
import java.util.Map;

import com.ETO.proj.TbBrand;

import entity.PageResult;
import entity.Result;

public interface BrandService {
	public List<TbBrand> findAll();
	public PageResult findPage(int pageIndex,int pageSize);
	public void add(TbBrand brand);
	public TbBrand findOne(long id);
	public void updateBrand(TbBrand brand);
	public void deleteBrand(long [] ids);
	public PageResult findPage(TbBrand brand,int pageIndex,int pageSize);
	public List<Map> selectOptionList();
}
