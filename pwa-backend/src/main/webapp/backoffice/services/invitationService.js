(function() {
	'use strict';
	angular.module('backofficeApp').service('InvitationService',invitationService);

	invitationService.$inject = [ '$resource' ];

	function invitationService($resource) {

		function list() {
			var invitations = [];
			for (var i = 0; i < 7; i++) {
				invitations.push({
					id : i,
					school : "ITIS",
					schoolClass : "2A",
					schoolYear : "2017",
					specialization : "IT",
					relatedUserId : "(not yet assigned)"
				});
			}
			return invitations;
		}

		return {
			list : list
		}
	}
})();