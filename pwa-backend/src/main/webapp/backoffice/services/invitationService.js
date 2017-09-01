angular.module('backofficeApp').factory('InvitationService',
		function($resource) {
			return $resource('/backoffice/invitation/:id', { id: '@_id' }, {
			    update: {
			      method: 'PUT'
			    }
			  });
		});
