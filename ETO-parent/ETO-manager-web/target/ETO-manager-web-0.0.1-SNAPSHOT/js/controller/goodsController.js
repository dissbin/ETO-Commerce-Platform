 //控制层 
app.controller('goodsController' ,function($scope,$controller,itemCatService   ,typeTemplateService,$location,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	$scope.reloadList = function(){
		$scope.searchGoods($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}
	//商品查询
	$scope.goodsSearch = {};
	$scope.searchGoods = function(pageIndex,pageSize){
		goodsService.searchGoods(pageIndex,pageSize,$scope.goodsSearch).success(
			function(response){
				$scope.list = response.rows;
				$scope.paginationConf.totalItems = response.total;
			}		
		)
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
				alert('hhaa');
			}
		);				
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
	//检测模板ID获取品牌列表、扩展属性
	$scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
		typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(
				function(response){
					
					$scope.typeTemplate = response;
					$scope.brandIds = JSON.parse($scope.typeTemplate.brandIds);
					                                                                                                                                                                                                                                                                                              goodsDesc.customAttributeItems = JSON.parse($scope.typeTemplete.customAttributeItems);
				}
		);
	});
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.ids=[];
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
	//状态显示
	$scope.status = ['未审核','已通过','未通过','已关闭'];
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
	//更新商品状态
	$scope.updateStatus = function(status){
		goodsService.updateStatus($scope.ids,status).success(
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
	//上传文件
	$scope.uploadFile = function(){
		uploadService.uploadFile().success(
				function(response){
					if(response.success){
						$scope.entity.pic = response.message;
					}else{
						alert('上传失败!');
					}				
				}
		).error(function() {           
        	     alert("上传发生错误");
        });
	}
});	
