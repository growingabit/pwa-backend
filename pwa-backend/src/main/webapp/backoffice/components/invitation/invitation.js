function InvitationController($state, InvitationService) {
	var ctrl = this;

	ctrl.save = save;
	ctrl.update = update;

	ctrl.invitations = InvitationService.query();

	function save(invitation) {
		console.log(invitation)
		console.log("creating...")
		InvitationService.save(invitation, function(data) {
			ctrl.invitations.push(data);
		});
	}
	
	function update(invitation){
		InvitationService.save(invitation, function(){
			alert("Gone");
		});
	}
}

angular.module('backofficeApp').component('invitation', {
	templateUrl : '/backoffice/components/invitation/invitation.html',
	controller : InvitationController
});