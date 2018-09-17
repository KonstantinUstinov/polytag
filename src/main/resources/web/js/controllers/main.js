var app = angular.module('polytags', [
    'base64',
    'ngCookies',
    'ui.ace',
    'ui.bootstrap',
    'ui.select',
    'ngSanitize',
    'ui.bootstrap.datetimepicker'
]);

app.service("configLoader", ["$http", "$location", ConfigLoader]);
app.service("utils", CommonUtilites);

app.directive("selectpicker", SelectPickerDirective);
app.directive('resizable', ResizableDirective);

app.controller('ListOfTags', ['$scope', '$http', '$modal', 'configLoader', ListOfTags]);


app.controller('MainController', ['$scope',  '$modal', '$http', function($scope,  $modal, $http){



}]);

app.controller('YesNoModalCtrl', function($scope, $modalInstance, header, message){

    $scope.modalHeader = header;
    $scope.modalMessage = message;

    $scope.ok = function(){
        $modalInstance.close();
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

app.controller('EditModalTagCtrl', function($scope, $modalInstance, editTag){

    $scope.editTag = editTag;

    $scope.ok = function(){
        $modalInstance.close($scope.editTag);
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

app.controller('CreateModalTagCtrl', function($scope, $modalInstance, createdTag, $http){

    $scope.createdTag = createdTag;

    $scope.ok = function(){
        $modalInstance.close($scope.createdTag);
    };

    $scope.generate = function(){

        if (typeof $scope.createdTag.originalTag != 'undefined' && $scope.createdTag.originalTag)
            if (typeof $scope.createdTag.name != 'undefined' && $scope.createdTag.name) {

                var url = "https://" + window.location.host  + "/api/tags/generate/";

                var newtag = {};
                newtag.name = $scope.createdTag.name;
                newtag.original = $scope.createdTag.originalTag;

                $http({method: 'POST', withCredentials: true, url: url, data: newtag , headers: {"Content-Type": "application/json;charset=UTF-8"}}).success(function(data){
                    data.dps = 'Nuviad';
                    $scope.createdTag = data;
                    $scope.createdTag.playId = data.playerIDs.join(", ")
                }).error(function(data, status){
                    console.log(data);
                    console.log(status);
                    console.log(url);
                });
            }
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});


