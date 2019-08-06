app.controller('baseController',function($scope){
			$scope.paginationConf = {			
					totalItems:10,
					itemsPerPage:10,
					perPageOptions:[10,20,30,40],
					onChange:function(){
						$scope.reloadList();
					}
			};
			
			$scope.ids = [];
			$scope.selectIds = function($event,id){
				if($event.target.checked){
					$scope.ids.push(id);
				}
				else{
					var index = $scope.ids.indexOf(id);
					$scope.ids.splice(index,1);
				}
				
			}
			
			//查询集合下的对象中的某个字段值是否存在
			$scope.searchObjectByKey = function(list,key,keyValue){
				for(var i=0;i<list.length;i++){
					if(list[i][key] == keyValue){
						return list[i];
					}
				}
				return null;
			}
});



