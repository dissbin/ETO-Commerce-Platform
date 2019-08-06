	app.controller("brandController", function($scope,$controller,brandService) {
		$controller('baseController',{$scope:$scope});
		
		$scope.findAll = function() {//定义方法名为findAll的方法

			brandService.findAll().success(function(response) {

				$scope.list = response;

			});
		}

		$scope.reloadList = function(){
			$scope.searchBrand($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		}
		//修改品牌
		$scope.saveBrand = function(){
			var operation = brandService.addBrand($scope.brand);
			if($scope.brand.id != null)
				operation = brandService.updateBrand($scope.brand);
			operation.success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(response.message);
					}
				}		
			);
		}
		//查询品牌
		$scope.findOne = function(id){
			brandService.findOne(id).success(
				function(response){
					$scope.brand = response;
				}		
			)
		}
		//删除品牌
		$scope.deleteBrand = function(){
			brandService.deleteBrand($scope.ids).success(
				function(response){
					if(response.success){
						$scope.reloadList();
					}else{
						alert(response.message);
					}
				}		
			)
		}
		

		//品牌查询
		$scope.brandSearch = {};
		$scope.searchBrand = function(pageIndex,pageSize){
			brandService.searchBrand(pageIndex,pageSize,$scope.brandSearch).success(
				function(response){
					$scope.list = response.rows;
					$scope.paginationConf.totalItems = response.total;
				}		
			)
		}
		
	});