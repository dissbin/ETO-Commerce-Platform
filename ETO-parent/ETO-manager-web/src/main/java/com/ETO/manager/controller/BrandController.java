package com.ETO.manager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ETO.proj.TbBrand;
import com.ETO.sellergoods.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;

import entity.PageResult;
import entity.Result;

@RestController
@RequestMapping("/brand")
public class BrandController {
	@Reference
	private BrandService brandService;
	@RequestMapping("/findAll")
	public List<TbBrand> findAll(){
		return brandService.findAll();
	}
	@RequestMapping("/findPage")
	public PageResult findPage(int pageIndex,int pageSize) {
		PageResult pageResult = brandService.findPage(pageIndex, pageSize);
		return pageResult;
	}
	@RequestMapping("/addBrand")
	public Result add(@RequestBody TbBrand brand) {
		try {
			brandService.add(brand);
			return new Result(true, "success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "false");
		}
	}
	@RequestMapping("/findOne")
	public TbBrand findOne(long id) {
		TbBrand brand = brandService.findOne(id);
		return brand;
	}
	@RequestMapping("/updateBrand")
	public Result updateBrand(@RequestBody TbBrand brand) {
		try {
			brandService.updateBrand(brand);
			return new Result(true, "保存成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "保存失败！");
		}
	}
	@RequestMapping("/deleteBrand")
	public Result deleteBrand(long [] ids) {
		try {
			brandService.deleteBrand(ids);
			return new Result(true, "删除成功！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "删除失败！");
		}
	}
	@RequestMapping("/searchBrand")
	public PageResult searchBrand(@RequestBody TbBrand brand,int pageIndex,int pageSize) {
		return brandService.findPage(brand,pageIndex,pageSize);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	@RequestMapping("/selectOptionList")
	public List<Map> selectOptionList(){
		return brandService.selectOptionList();
	}
}
