app.controller('searchController',function($scope,searchService){
	$scope.mapSearch = {'keywords':'','category':'','brand':'','spec':{},'priceRange':'','pageIndex':1,'pageSize':30};
	searchRecords = [];
	$scope.search = function(isSearch){
		if($scope.mapSearch['keywords'] != null)
			if(isSearch){
				$scope.mapSearch.pageIndex = 1;
			}
			$scope.mapSearch.pageIndex = parseInt($scope.mapSearch.pageIndex);
			searchRecords.push($scope.mapSearch['keywords']);
			searchService.search($scope.mapSearch).success(
				function(response){
					$scope.resultMap = response;
					buildPageLabel();
				}
		);
	}
	buildPageLabel = function(){
		//5个页码动态改变
		$scope.pageLabel = [];
		var firstPage = 1;
		var lastPage = $scope.resultMap.totalPages;
		//省略号
		$scope.firstDot = true;
		$scope.lastDot = true;
		//若页总数大于5则分类讨论
		if($scope.resultMap.totalPages > 5){
			if($scope.mapSearch.pageIndex < 3){//中心页靠前
				$scope.firstDot = false;//更新点
				lastPage = 5;
			}
			else if($scope.mapSearch.pageIndex > $scope.resultMap.totalPages-2){//中心页靠后
				$scope.lastDot = false;//更新点
				firstPage = $scope.resultMap.totalPages-4;
			}
			else{//一般情况
				firstPage = $scope.mapSearch.pageIndex - 2;
				lastPage = $scope.mapSearch.pageIndex + 2;
			}
		}else{
			$scope.firstDot = false;
			$scope.lastDot = false;
		}

		for(i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	$scope.addParameter = function(key,value){
		if(key=='category' || key=='brand' || key=='priceRange'){
			$scope.mapSearch[key] = value;
		}
		else{
			$scope.mapSearch.spec[key] = value;
		}
		if(key == 'brand'){
			$scope.mapSearch['keywords'] = value;
		}
		$scope.search(true);
	}
	
	$scope.removeParam = function(key){
		if(key=='category' || key=='brand' || key=='priceRange')
			$scope.mapSearch[key] = "";
		else{
			delete $scope.mapSearch.spec[key];
		} 
		if(key=='brand'){
			searchRecords.pop();
			$scope.mapSearch['keywords'] = searchRecords[0];
		}
		$scope.search(true);
	}
	
	$scope.queryByPage = function(pageIndex){
		if(pageIndex<1 || pageIndex>$scope.resultMap.totalPages){
			return;
		}
		$scope.mapSearch.pageIndex = pageIndex;
		$scope.search(false);
	}
	
	$scope.isTopPage = function(){
		if($scope.mapSearch.pageIndex == 1)
			return true;
		else
			return false;
	}
	$scope.isEndPage = function(){
		if($scope.mapSearch.pageIndex == $scope.resultMap.totalPages)
			return true;
		else
			return false;
	}
	

});