package com.ETO.search.service;

import java.util.List;
import java.util.Map;

import org.w3c.dom.ls.LSInput;

import com.ETO.proj.TbItem;

public interface ItemSearchService {
	public Map search(Map map);
	public void saveListToSolr(List<TbItem> itemList);
	public void deleteByGoodsId(Long[] ids);
}
