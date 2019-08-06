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
		});



