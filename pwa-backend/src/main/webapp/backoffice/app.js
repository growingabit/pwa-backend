var app = angular.module('backofficeApp', [ 'ui.router', 'ngResource', 'auth0.auth0', 'angular-jwt' ]);

app.config(config);

config.$inject = [ '$stateProvider', '$urlRouterProvider', '$locationProvider', '$httpProvider', 'angularAuth0Provider', 'jwtOptionsProvider' ];

function config($stateProvider, $urlRouterProvider, $locationProvider, $httpProvider, angularAuth0Provider, jwtOptionsProvider) {

    var auth0CLientID;
    var auth0Domain;
    var auth0CallbakUrl;

    var origin = window.location.origin;
    if (origin.indexOf('localhost') > -1) {
	auth0CLientID = "32wl1L4tlptjPImhEvLIbrQSkwmAJx5s";
	auth0Domain = "growbit-development.eu.auth0.com";
	auth0CallbakUrl = "http://localhost:8080/backoffice/index.html#/callback";
    } else if (origin.indexOf('growbit-0-dev') > -1) {
	auth0CLientID = "32wl1L4tlptjPImhEvLIbrQSkwmAJx5s";
	auth0Domain = "growbit-development.eu.auth0.com";
	auth0CallbakUrl = "https://growbit-0-dev.appspot.com/backoffice/index.html#/callback";
    } else if (origin.indexOf('api.growbit.xyz') > -1) {
	auth0CLientID = "aB4EbELMT7MHTwZRDv2ivV5TIFItysL6";
	auth0Domain = "growbit.auth0.com";
	auth0CallbakUrl = "https://api.growbit.xyz/backoffice/index.html#/callback";
    }

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
    });

    // Initialization for the angular-auth0 library
    angularAuth0Provider.init({
	clientID : auth0CLientID,
	domain : auth0Domain,
	responseType : 'token id_token',
	audience : 'https://' + auth0Domain + '/userinfo',
	redirectUri : auth0CallbakUrl,
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
