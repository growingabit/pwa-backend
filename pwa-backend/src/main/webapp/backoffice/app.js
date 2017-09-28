var app = angular.module('backofficeApp',
    ['ui.router', 'ngResource', 'auth0.auth0', 'angular-jwt', 'environment']);

app.config(config);

config.$inject = ['$stateProvider', '$urlRouterProvider', '$locationProvider',
  '$httpProvider', 'angularAuth0Provider', 'jwtOptionsProvider',
  'envServiceProvider'];

function config($stateProvider, $urlRouterProvider, $locationProvider,
    $httpProvider, angularAuth0Provider, jwtOptionsProvider,
    envServiceProvider) {

  envServiceProvider.config({
    domains: {
      local: ['localhost'],
      development: ['growbit-0-dev.appspot.com'],
      production: ['https://abc.growbit.xyz'],
    },
    vars: {
	local: {
        auth0CLientID: '32wl1L4tlptjPImhEvLIbrQSkwmAJx5s',
        auth0Domain: 'growbit-development.eu.auth0.com',
        auth0CallbakUrl: 'http://localhost:8080/backoffice/index.html#/callback'
      },
      development: {
	  auth0CLientID: '32wl1L4tlptjPImhEvLIbrQSkwmAJx5s',
	  auth0Domain: 'growbit-development.eu.auth0.com',
	  auth0CallbakUrl: 'https://growbit-0-dev.appspot.com/backoffice/index.html#/callback'
      },
      production: {
        auth0CLientID: 'aB4EbELMT7MHTwZRDv2ivV5TIFItysL6',
        auth0Domain: 'growbit.auth0.com',
        auth0CallbakUrl: 'https://abc.growbit.xyz/backoffice/index.html#/callback'
      }
    }
  });

  $stateProvider.state({
    name: 'home',
    url: '/home',
    component: 'home'
  }).state({
    name: 'invitation',
    url: '/invitation',
    component: 'invitation'
  }).state({
    name: 'callback',
    url: '/callback',
    component: 'callback'
  });

  // Initialization for the angular-auth0 library
  angularAuth0Provider.init({
    clientID: envServiceProvider.read('auth0CLientID'),
    domain: envServiceProvider.read('auth0Domain'),
    responseType: 'token id_token',
    audience: 'https://' + envServiceProvider.read('auth0Domain') + '/userinfo',
    redirectUri: envServiceProvider.read('auth0CallbakUrl'),
    scope: 'openid'
  });

  $locationProvider.hashPrefix('');

  $urlRouterProvider.otherwise('home');

  jwtOptionsProvider.config({
    tokenGetter: function () {
      return localStorage.getItem('id_token');
    }
  });

  $httpProvider.interceptors.push('jwtInterceptor');

  // run the environment check, so the comprobation is made
  // before controllers and services are built
  envServiceProvider.check();
}

app.run(run);
run.$inject = ['authService'];

function run(authService) {
  authService.handleAuthentication();
}
