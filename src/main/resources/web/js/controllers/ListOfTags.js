function ListOfTags($scope, $http, $modal, configLoader){

    $scope.tags = [];

    $scope.init = function(){
        var date = new Date();
        date.setFullYear( date.getFullYear() - 3 );
        $scope.toCreationDate = new Date();
        $scope.fromCreationDate = date;

        $scope.toUpdateDate = new Date();;
        $scope.fromUpdateDate = date;
        $scope.refreshData();
    };

    $scope.toISODate = function(date) {
        return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
    };

    $scope.refreshData = function(){
        var toCreationDate = $scope.toISODate($scope.toCreationDate);
        var fromCreationDate = $scope.toISODate($scope.fromCreationDate);

        var toUpdateDate = $scope.toISODate($scope.toUpdateDate);
        var fromUpdateDate = $scope.toISODate($scope.fromUpdateDate);

        var url = "https://" + window.location.host + "/api/tags/search" +
            "?limit=50" +
            "&creationdatefrom=" + fromCreationDate +
            "&creationdateto=" + toCreationDate +
            "&updatedatefrom=" + fromUpdateDate +
            "&updatedateto=" + toUpdateDate;

        if (typeof $scope.dsps != 'undefined' && $scope.dsps != 'ALL') {
            url += '&dsp=' + $scope.dsps;
        }

        if (typeof $scope.polytagid != 'undefined' && $scope.polytagid)
        {
            url += "&polytagid=" + $scope.polytagid;
        }

        if(typeof $scope.playerid != 'undefined' && $scope.playerid)
        {
            url += "&playerid=" + $scope.playerid;
        }

        $http({method: 'GET', withCredentials: true, url: url}).success(function(data){
            $scope.tags = data
        }).error(function(err){
            $scope.tags = [];
            console.log(err);
        });

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