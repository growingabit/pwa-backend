function HomeController(authService) {
	var ctrl = this;
	ctrl.authService = authService;
	
	ctrl.logout = logout;
	
	function logout(){
		authService.logout();
		location.reload();
	}
}

angular.module('backofficeApp').component('home', {
	templateUrl : '/backoffice/components/home/home.html',
	controller : HomeController
});