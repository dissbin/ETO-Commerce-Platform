package com.ETO.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ETO.mapper.TbBrandMapper;
import com.ETO.mapper.TbGoodsDescMapper;
import com.ETO.mapper.TbGoodsMapper;
import com.ETO.mapper.TbItemCatMapper;
import com.ETO.mapper.TbItemMapper;
import com.ETO.mapper.TbSellerMapper;
import com.ETO.proj.TbBrand;
import com.ETO.proj.TbGoods;
import com.ETO.proj.TbGoodsExample;
import com.ETO.proj.TbItem;
import com.ETO.proj.TbItemCat;
import com.ETO.proj.TbItemExample;
import com.ETO.proj.TbSeller;
import com.ETO.proj.TbGoodsExample.Criteria;
import com.ETO.projgroup.Goods;
import com.ETO.sellergoods.service.GoodsService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
	
	/**
	 * 增加
	 */
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置商品状态为未审核
		goodsMapper.insert(goods.getGoods());//插入商品扩展信息
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//获取主要商品信息的id来设置商品扩展信息的id
		goodsDescMapper.insert(goods.getGoodsDesc());//插入主要商品信息
		
		//插入库存等信息
		saveItemList(goods);

		
	}
	
	private void saveItemList(Goods goods) {
		if("1".equals(goods.getGoods().getIsEnableSpec())) {
			for(TbItem tbItem: goods.getItemList()) {
				//插入商品完整名称
				String title = goods.getGoods().getGoodsName();
				Map<String,Object> specs = JSON.parseObject(tbItem.getSpec());
				for(String key:specs.keySet()) {//循环键以取得键值
					title += " " + specs.get(key);
				}
				tbItem.setTitle(title);
				//插入价格
				tbItem.setPrice(goods.getGoods().getPrice());
				setBaseItem(goods,tbItem);
				itemMapper.insert(tbItem);//逐个插入库存列表
			}
		}else {
			TbItem tbItem = new TbItem();
			//标题
			tbItem.setTitle(goods.getGoods().getGoodsName());
			//价格
			tbItem.setPrice(goods.getGoods().getPrice());
			//库存
			tbItem.setNum(9999);
			//状态
			tbItem.setStatus("1");
			//是否默认
			tbItem.setIsDefault("1");
			setBaseItem(goods,tbItem);//设置Item其他字段
			tbItem.setSpec("{}");
			itemMapper.insert(tbItem);
		}
	}
	
	private void setBaseItem(Goods goods,TbItem tbItem) {
		//插入图片
		List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(),Map.class);//字符串转JSON数组(集合套Map)
		if(list.size() >0) {
			tbItem.setImage((String)list.get(0).get("url"));
		}
		//插入类型ID(第三级)
		tbItem.setCategoryid(goods.getGoods().getCategory3Id());
		//商品增加时间
		tbItem.setCreateTime(new Date());
		//商品更新时间
		tbItem.setUpdateTime(new Date());
		//商品ID
		tbItem.setGoodsId(goods.getGoods().getId());
		//卖家ID
		tbItem.setSellerId(goods.getGoods().getSellerId());
		//类型名称
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid());
		tbItem.setCategory(itemCat.getName());
		//品牌名称
		TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		System.out.println("品牌是:"+brand);
		tbItem.setBrand(brand.getName());
		//卖家名称
		TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		tbItem.setSeller(seller.getNickName());
	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//更新商品基础信息
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//更新商品扩展信息
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//删除原有商品选中规格信息
		TbItemExample example = new TbItemExample();
		com.ETO.proj.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//插入新的
		saveItemList(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		goods.setGoods(goodsMapper.selectByPrimaryKey(id));
		goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));
		TbItemExample example = new TbItemExample();
		com.ETO.proj.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		goods.setItemList(itemMapper.selectByExample(example));
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods good = goodsMapper.selectByPrimaryKey(id);
			good.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(good);
		}		
	}
	
	
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
			if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids, String status) {
		for(int i=0;i<ids.length;i++) {
			TbGoods good = goodsMapper.selectByPrimaryKey(ids[i]);
			good.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(good);
		}
		
	}

	@Override
	public void updateMarkable(Long[] ids) {
		System.out.println("ssss");
		for(int i=0;i<ids.length;i++) {
			TbGoods good = goodsMapper.selectByPrimaryKey(ids[i]);
			good.setIsMarketable("1");
			goodsMapper.updateByPrimaryKey(good);
		}
		
	}

	//查询新添加的商品
	@Override
	public List<TbItem> findNewGoods(Long[] ids, String status) {
		TbItemExample example = new TbItemExample();
		com.ETO.proj.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		criteria.andGoodsIdIn(Arrays.asList(ids));
		return itemMapper.selectByExample(example);
	}


	
}
