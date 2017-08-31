let app = angular.module('backofficeApp', [ 'ui.router', 'ngResource', 'auth0.auth0', 'angular-jwt' ])

app.config(config);

config.$inject = [ '$stateProvider', '$urlRouterProvider', '$locationProvider',
		'$httpProvider', 'angularAuth0Provider', 'jwtOptionsProvider' ];

function config($stateProvider, $urlRouterProvider, $locationProvider,
		$httpProvider, angularAuth0Provider, jwtOptionsProvider) {

	$stateProvider.state({
		name : 'home',
		url : '/home',
		component : 'home'
	}).state({
		name : 'invitation',
		url : '/invitation',
		component : 'invitation'
	}).state({
		name : 'callback',
		url : '/callback',
		component : 'callback'
	})

	// Initialization for the angular-auth0 library
	angularAuth0Provider.init({
		clientID : AUTH0_CLIENT_ID,
		domain : AUTH0_DOMAIN,
		responseType : 'token id_token',
		audience : 'https://' + AUTH0_DOMAIN + '/userinfo',
		redirectUri : AUTH0_CALLBACK_URL,
		scope : 'openid'
	});

	$locationProvider.hashPrefix('');
	
	$urlRouterProvider.otherwise('home');

	jwtOptionsProvider.config({
		tokenGetter : function() {
			return localStorage.getItem('id_token');
		}
	});

	$httpProvider.interceptors.push('jwtInterceptor');
}

app.run(run);
run.$inject = [ 'authService' ];

function run(authService) {
	authService.handleAuthentication();
}