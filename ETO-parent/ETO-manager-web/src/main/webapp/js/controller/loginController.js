app.controller('loginController',function($scope,loginService){
	loginService.showLoginName().success(function(response){
		$scope.loginName = response.loginName;
	});
});