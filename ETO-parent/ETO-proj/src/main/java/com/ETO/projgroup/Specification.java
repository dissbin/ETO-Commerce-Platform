package com.ETO.projgroup;

import java.io.Serializable;
import java.util.List;

import com.ETO.proj.TbSpecification;
import com.ETO.proj.TbSpecificationOption;

public class Specification implements Serializable{
	public TbSpecification getTbSpecification() {
		return tbSpecification;
	}
	public void setTbSpecification(TbSpecification tbSpecification) {
		this.tbSpecification = tbSpecification;
	}

	
	public List<TbSpecificationOption> getTbSpecificationOptionList() {
		return tbSpecificationOptionList;
	}
	public void setTbSpecificationOptionList(List<TbSpecificationOption> tbSpecificationOptionList) {
		this.tbSpecificationOptionList = tbSpecificationOptionList;
	}
	//规格名称类
	private TbSpecification tbSpecification;
	//规格参数列表
	private List<TbSpecificationOption> tbSpecificationOptionList;
}
