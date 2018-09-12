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



