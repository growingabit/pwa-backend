function InvitationCodeListController($state) {
    var ctrl = this;
    
    ctrl.create = create;

    ctrl.invitationCodes = [];
    for (var i = 0; i < 7; i++) {
        ctrl.invitationCodes.push({
        		id : i,
        		school : "ITIS",
        		schoolClass : "2A",
        		schoolYear : "2017",
        		specialization : "IT"
        });
    }
    
    function create(invitation){
    		console.log("created")
    		console.log(invitation)
    }
}

angular.module('backofficeApp').component('invitationCodeList', {
    templateUrl: '/backoffice/components/invitation-code/invitationCodeList.html',
    controller: InvitationCodeListController
});