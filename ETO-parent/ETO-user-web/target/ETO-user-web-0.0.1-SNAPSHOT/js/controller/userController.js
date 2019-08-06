 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$scope.reg = function(){
		if($scope.entity.password != $scope.password){
			$scope.entity.password = "";
			$scope.password = "";
			return;
		}
		userService.add($scope.entity,$scope.inputCode).success(
			function(response){
				alert(response.message);
			}
		);
	}
	
	$scope.createAndSendSmsCode = function(){
		userService.createAndSendSmsCode($scope.entity.phone).success(
				function(response){
					alert(response.message);
				}
		);
	}
});	
