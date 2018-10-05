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
app.service('authCook', ['$cookies', authCook]);

app.directive("selectpicker", SelectPickerDirective);
app.directive('resizable', ResizableDirective);

app.controller('ListOfTags', ['$scope', '$http', '$modal', 'configLoader', ListOfTags]);


app.controller('MainController', ['$scope', '$cookies', 'authCook', '$modal', '$http', function($scope, $cookies, authCook, $modal, $http){

    $scope.clientName = $cookies.user || "";

    if ($scope.clientName == "") {
        authCook.clearAuthorizationToken();
        window.location.href = window.location.origin + '/login/index.html';
    }

    $scope.logout = function(){
        window.location = "/logout"
    };

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

app.controller('EditModalTagCtrl', function($scope, $modalInstance, editTag, $http){

    $scope.editTag = editTag;

    $scope.dpschange = function(){
        if (typeof $scope.editTag.originalTag != 'undefined' && $scope.editTag.originalTag){

            if ($scope.editTag.DSPs === 'NA')
                return;

            var url = "https://" + window.location.host  + "/api/tags/generate/";

            var number = Math.random(); // 0.9394456857981651
            number.toString(36); // '0.xtis06h6'
            var id = number.toString(36).substr(2, 9); // 'xtis06h6'

            var newtag = {};
            newtag.name = id;
            newtag.original = $scope.editTag.originalTag;
            newtag.dsp = $scope.editTag.DSPs;

            $http({method: 'POST', withCredentials: true, url: url, data: newtag , headers: {"Content-Type": "application/json;charset=UTF-8"}}).success(function(data){

                var re = new RegExp(data.id, 'g');
                $scope.editTag.polyTag = data.polyTag.replace(re, $scope.editTag.tagId);;

            }).error(function(data, status){
                console.log(data);
                console.log(status);
                console.log(url);
            });

        }

    };

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

    $scope.dpschange = function(){
        $scope.generate();
    };

    $scope.generate = function(){

        if (typeof $scope.createdTag.originalTag != 'undefined' && $scope.createdTag.originalTag)
            if (typeof $scope.createdTag.name != 'undefined' && $scope.createdTag.name) {

                var url = "https://" + window.location.host  + "/api/tags/generate/";

                var newtag = {};
                newtag.name = $scope.createdTag.name;
                newtag.original = $scope.createdTag.originalTag;
                newtag.dsp = $scope.createdTag.dps;

                $http({method: 'POST', withCredentials: true, url: url, data: newtag , headers: {"Content-Type": "application/json;charset=UTF-8"}}).success(function(data){
                    $scope.createdTag = data;
                    $scope.createdTag.dps = data.DSPs[0];
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


