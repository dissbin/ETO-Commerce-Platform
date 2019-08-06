package com.ETO.projgroup;

import java.io.Serializable;
import java.util.List;

import com.ETO.proj.TbGoods;
import com.ETO.proj.TbGoodsDesc;
import com.ETO.proj.TbItem;

public class Goods implements Serializable{
	private TbGoods goods;//商品主要信息
	private TbGoodsDesc goodsDesc;//商品扩展信息
	private List<TbItem> itemList;//商品相关信息
	
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}


}
