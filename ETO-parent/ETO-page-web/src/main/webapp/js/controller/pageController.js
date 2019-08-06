app.controller('pageController',function($scope){
	$scope.addNum = function(x){
		$scope.num += x;
		if($scope.num < 0)
			$scope.num = 0;
	}
	//选中的规格
	$scope.specItems = {};
	$scope.selectSpec = function(key,value){
		$scope.specItems[key] = value;
		$scope.searchSku();
	}
	$scope.isSelected = function(key,value){
		if($scope.specItems[key] == value)
			return true;
		else
			return false;
	}

	//设置默认sku(含规格)
	$scope.sku = {};//选中的商品sku
	$scope.setDefaultSku = function(){
		$scope.sku = SKUList[0];
		//深度克隆：不能让修改选中规格影响sku列表
		$scope.specItems =  JSON.parse(JSON.stringify(SKUList[0].spec));
	}

	//检测两个对象是否相等
	testEqual = function(map1,map2){
		//对两个对象的所有key进行循环匹配
		for(var k in map1){
			if(map1[k] != map2[k])
				return false;
		}
		for(var k in map2){
			if(map2[k] != map1[k])
				return false;
		}
		return true;
	}

	//根据specItems的规格在SKULit列表中查找相同的规格
	$scope.searchSku = function(){
		for(var i=0;i<SKUList.length;i++){
			if(testEqual($scope.specItems,SKUList[i].spec)){
				//修改时不会直接修改SKUList的数据，只会从SKUList中寻找匹配的sku
				$scope.sku = SKUList[i];
				return;
			}
		}
	}

	//加入购物车
	$scope.addToCart = function(){
		alert('skuId:'+$scope.sku.id);
	}
});