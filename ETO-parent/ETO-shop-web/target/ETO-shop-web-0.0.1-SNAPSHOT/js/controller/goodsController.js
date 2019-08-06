 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location   ,goodsService,uploadService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.reloadList = function(){
		$scope.searchGoods($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	$scope.goodsSearch = {};
	$scope.searchGoods = function(pageIndex,pageSize){
		goodsService.searchGoods(pageIndex,pageSize,$scope.goodsSearch).success(
			function(response){
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;
			}		
		)
	}
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){	
		var id =  $location.search()['id'];	//参数传递
		if(id==null){//如果没有ID表明前往增加页面
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//显示富编辑器内容
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片列表
				$scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
				$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				//转换规格
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
				//转换选中列表
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
				}
				
			}
		);				
	}
	$scope.save = function(){
		var saveObject;
		if($scope.entity.goods.id !=null)
			saveObject = goodsService.update($scope.entity);//更新商品
		else
			saveObject = goodsService.add( $scope.entity );//增加商品
		saveObject.success(
			function(response){
				if(response.success){
					alert('成功！');
					location.href='goods.html';
					
				}else{
					alert(response.message);
				}
			}		
		);
	}
	
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	
	
    //上传文件
	$scope.uploadFile = function(){
		uploadService.uploadFile().success(
				function(response){
					if(response.success){
						$scope.image_entity.url = response.message;
					}else{
						alert('上传失败!');
					}				
				}
		).error(function() {           
        	     alert("上传发生错误");
        });
	}
	//图片显示列表
	$scope.entity = {goodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add_image_entity = function(){
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
	}
	//移除图片
	$scope.remove_image_entity = function(index){
		$scope.entity.goodsDesc.itemImages.splice(index,1);
	}
	//一级商品分类列表
	$scope.selectItem1List = function(){
		itemCatService.findByParentId(0).success(
				function(response){
					$scope.item1List = response;
				}
		);
	}
	//二级商品分类列表	
	$scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.item2List = response;
				}
		);
	});
	//三级商品分类列表	
	$scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
		itemCatService.findByParentId(newValue).success(
				function(response){
					$scope.item3List = response;
				}
		);
	});
	//根据categoryID查询类型模板ID
	$scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
		itemCatService.findOne($scope.entity.goods.category3Id).success(
				function(response){
					$scope.entity.goods.typeTemplateId = response.typeId;
				}
		);
	});
	//检测模板ID获取品牌列表、扩展属性
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(
				function(response){
					alert(JSON.stringify(response));
					$scope.typeTemplate = response;
					$scope.brandIds = JSON.parse($scope.typeTemplate.brandIds);
					if($location.search()['id'] == null)
						$scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);                                                                                                                                                                                                                                                                                                goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplete.customAttributeItems);
				}
		);
		//查询规格ID选项列表
		typeTemplateService.findSpecIds(newValue).success(
				function(response){
					$scope.specIds = response;
				}
		);
	});
	//选择规格
	$scope.addSpecItems = function($event,text,value){
		var item = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',text);
		if(item != null){
			if($event.target.checked){
				item.attributeValue.push(value);
			}else{
				item.attributeValue.splice(item.attributeValue.indexOf(value),1);
				if(item.attributeValue.length == 0){ 
					$scope.entity.goodsDesc.specificationItems.splice(
							$scope.entity.goodsDesc.specificationItems.indexOf(item),1);
				}
			}
			
		}else{
			$scope.entity.goodsDesc.specificationItems.push({'attributeName':text,'attributeValue':[value]});
		}
	}
	
	//生成选中规格商品的库存等情况列表
	$scope.createItemList = function(){
		$scope.entity.itemList = [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];//要返回的列表
		var specificItems = $scope.entity.goodsDesc.specificationItems;//选中的多个不同的规格的列表
		for(var i=0;i<specificItems.length;i++){//循环新添加的不同的规格
			$scope.entity.itemList = addColumn($scope.entity.itemList,specificItems[i].attributeName,specificItems[i].attributeValue);			
		}

	}
	
	addColumn = function(list,colName,colValues){
		
		var newList = [];
		for(var i=0;i<list.length;i++){//循环原来的不同规格的值
			var oldRow = list[i];
			for(var j=0;j<colValues.length;j++){//循环另一个规格的值集合中的值
				newRow = JSON.parse(JSON.stringify(oldRow));
				newRow.spec[colName] = colValues[j];
				newList.push(newRow);		
			}
		}
		return newList;
	}
	
	//状态显示
	$scope.status = ['未审核','已审核','未通过','已关闭'];
	//显示分类名称
	$scope.itemCats = [];
	$scope.findItemCats = function(){
		itemCatService.findAll().success(
				function(response){
					for(var i=0;i<response.length;i++){
						$scope.itemCats[response[i].id] = response[i].name;//itemCats中类型的下标与类型的ID相等						
					}
				}
		);
	}
	//显示选中的规格
	$scope.checkAttribute = function(attributeName,value){
		var specItems = $scope.entity.goodsDesc.specificationItems;
		var object = $scope.searchObjectByKey(specItems,'attributeName',attributeName);
		if(object == null)
			return false;
		if(object.attributeValue.indexOf(value) >=0)
			return true;
		return false;
	}
	//商品上下架
	$scope.updateMarkable = function(){
		goodsService.updateMarkable($scope.ids).success(
			function(response){
				if(response.success){
					$scope.ids = [];
					$scope.reloadList();
				}
				else
					alert(response.message);	
			}
		);
	}
});	
