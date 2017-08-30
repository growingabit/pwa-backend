function InvitationController($state, InvitationService) {
    var ctrl = this;
    
    ctrl.create = create;

    ctrl.invitations = InvitationService.list();
    
    function create(invitation){
    		console.log("created")
    		console.log(invitation)
    }
}

angular.module('backofficeApp').component('invitation', {
    templateUrl: '/backoffice/components/invitation/invitation.html',
    controller: InvitationController
});