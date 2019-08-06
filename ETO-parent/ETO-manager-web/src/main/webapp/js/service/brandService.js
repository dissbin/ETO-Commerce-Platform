app.service("brandService",function($http){
		this.findAll = function(){
			return $http.get('../brand/findAll.do');
		}
		this.addBrand = function(brand){
			return $http.post('../brand/addBrand.do',brand);
		}
		this.updateBrand = function(brand){
			return $http.post('../brand/updateBrand.do',brand);
		}
		this.findOne = function(id){
			return $http.get('../brand/findOne.do?id=' + id);
		}
		this.deleteBrand = function(ids){
			return $http.get('../brand/deleteBrand.do?ids=' + ids);
		}
		this.searchBrand = function(pageIndex,pageSize,brandSearch){
			return $http.post('../brand/searchBrand.do?pageIndex='+pageIndex+'&pageSize='+pageSize,brandSearch);
		}
		this.selectOptionList = function(){
			return $http.post('../brand/selectOptionList.do');
		}
});