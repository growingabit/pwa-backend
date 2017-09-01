let app = angular.module('backofficeApp', [ 'ui.router', 'ngResource'])

app.config(config);

config.$inject = [ '$stateProvider', '$urlRouterProvider' ];

function config($stateProvider, $urlRouterProvider) {
	
	$stateProvider.state({
		name : 'invitation',
		url : '/invitation',
		component : 'invitation'
	})
	
}