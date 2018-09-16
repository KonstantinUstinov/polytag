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



