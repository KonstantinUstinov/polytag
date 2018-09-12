function ListOfTags($scope, $http, $modal, configLoader){

    $scope.tags = [];

    $scope.init = function(){
        var date = new Date();
        $scope.toCreationDate = new Date();;
        $scope.fromCreationDate = date.setFullYear( date.getFullYear() - 3 );

        $scope.toUpdateDate = new Date();;
        $scope.fromUpdateDate = date;
    };


    $scope.refreshData = function(){

    };

    $scope.create = function(){

    };

    $scope.toNowCreation = function(){
        $scope.toCreationDate = new Date();
    };

    $scope.toNowUpdate = function() {
        $scope.toUpdateDate = new Date();
    }
}