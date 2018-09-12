var app = angular.module('bespoke', [
    'base64',
    'ngCookies',
    'ui.ace',
    'ui.bootstrap',
    'ui.select',
    'ngSanitize',
    'ui.bootstrap.datetimepicker',
    'ngIdle'
]);

app.service("configLoader", ["$http", "$location", ConfigLoader]);
app.service("utils", CommonUtilites);
app.service('auth', ['$cookies', auth]);

app.factory('parsingService', function() {
  return {
    parsePorts: function(input) {
        input = (input || "") + "";
        var ports = [];

        function validatePort(port) {
            return 0 <= port && port <= 65535;
        };

        if ((/^\s*(\d+\s*-\s*\d+|\d+)(\s*,\s*(\d+\s*-\s*\d+|\d+))*\s*$/).test(input)) {
            input.replace((/(^|,)\s*((\d+)\s*-\s*(\d+)|\d+)\s*/g), function(_, __, value, min, max) {
                if (typeof(min) !== 'undefined') {
                    if (validatePort(min) && validatePort(max) && (min <= max)) {
                        for (var port = min; port <= max; port++) {
                            ports.push(port);
                        }
                    }
                    else return false;
                }
                else if (validatePort(value)) {
                    ports.push(value);
                }
                else return false;
            });
        }
        else return false;

        return (ports.length > 0) ? ports : false;
    }
  };
});


app.directive("selectpicker", SelectPickerDirective);
app.directive('resizable', ResizableDirective);

app.controller('ConnectedWorkers', ['$scope', '$http', '$modal', '$timeout', 'configLoader', 'auth', 'parsingService', ConnectedWorkers]);

app.controller('ClusterController', ['$scope', '$location', '$cookies', 'auth', '$log', 'Idle', '$modal', '$http',
    function($scope, $location, $cookies, auth, $log, Idle, $modal, $http){

    $scope.clientName = "User";
    $scope.isOpen = [1];

    if ($scope.clientName == "") {
        auth.clearAuthorizationToken();
        window.location.href = window.location.origin
    }

    $scope.accessToken = auth.getStoredAuthorizationToken();

    Idle.watch();

    $scope.$on("upReloadWorkers", function(ev, message){
        $scope.$broadcast("downReloadWorkers", {})
    });

    $scope.logout = function(){
        window.location = "/logout"
    };

    $scope.$on('IdleStart', function() {
        $log.debug('IdleStart');
    });

    $scope.$on('IdleEnd', function() {
        $log.debug('IdleEnd');
    });

    $scope.$on('IdleTimeout', function() {
        $log.debug('IdleTimeout');
        window.location.href = "/logout"
    });

}]);

app.controller('WorkerController', function($scope, $modalInstance, newWorker, parsingService){

    $scope.newWorker = newWorker;
    $scope.isEditMode = newWorker.isEditMode;

    $scope.isInvalidValidPort = function(){
        return parsingService.parsePorts($scope.newWorker.port) === false;
    };

    $scope.ok = function(){
        $modalInstance.close($scope.newWorker);
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

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

app.config(function(IdleProvider, KeepaliveProvider) {
    IdleProvider.idle(GLOBAL_ENV_CONFIG.timeOut);
    IdleProvider.timeout(5);
    KeepaliveProvider.interval(10);
});

