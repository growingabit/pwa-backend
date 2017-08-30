let app = angular.module('backofficeApp', [ 'ui.router' ]);

app.config(config);
config.$inject = [ '$stateProvider', '$urlRouterProvider' ];

function config($stateProvider, $urlRouterProvider) {
	
	$stateProvider.state({
		name : 'invitationCodeList',
		url : '/invitationCode',
		component : 'invitationCodeList'
	})
	
}