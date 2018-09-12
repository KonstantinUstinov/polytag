var app = angular.module('bespoke', [
    'base64',
    'ngCookies',
    'ui.ace',
    'ui.bootstrap',
    'ui.select',
    'ngSanitize',
    'ui.bootstrap.datetimepicker',
    'ngFileUpload'
]);

app.service("configLoader", ["$http", "$location", ConfigLoader]);
app.service("utils", CommonUtilites);
app.service('auth', ['$cookies', auth]);

app.directive("selectpicker", SelectPickerDirective);
app.directive('resizable', ResizableDirective);

app.controller('CodeEditor', ['$scope', '$http', '$q', 'configLoader', 'auth', CodeEditor]);
app.controller('ExecuteRequest', ['$scope', '$http', ExecuteRequest]);
app.controller('Logs', ['$scope', '$http', '$timeout', Logs]);
app.controller('SelectSnippet', ['$scope', '$http', '$modal', 'configLoader', 'auth', 'Upload', SelectSnippet]);

app.controller('MainController', ['$scope', '$location', '$cookies', 'auth', '$log', 'Idle', '$modal', '$http', function($scope, $location, $cookies, auth, $log, Idle, $modal, $http){

   /* $scope.clientName = $cookies.user || "";

    if ($scope.clientName == "") {
        auth.clearAuthorizationToken();
        window.location.href = window.location.origin
    }

    $scope.accessToken = auth.getStoredAuthorizationToken();

    $scope.isOpen = [1, 0, 0, 0];

    $scope.snippet = {
        path: "",
        description: "",
        code: "",
        version: 0,
        lang: "bsh"
    };

    $scope.showExportResult = function(exportResult) {
        $log.debug(exportResult) ;
        var modalInstance = $modal.open({
            templateUrl: 'templates/InfoContent.html',
            controller: 'InfoCtrl',
            resolve: {
                header: function(){
                    return "Resource promotion";
                },
                message: function(){
                    return exportResult.msg;
                }
            }
        });
    };

    if($cookies.ExportResult)
    {
        //$log.debug($cookies.ExportResult);
        var jobj = JSON.parse($cookies.ExportResult);
        // s"""{"token":\"$token\","url":"$destUrl","path":"$path","ver":$version}"""

        // get the snippet from the source env
        $http({ method: 'GET',
            headers: {'Authorization': "Bearer " + jobj.token},
            url: jobj.url + "/resources/snippet/" + jobj.ver + "/" + jobj.path
        }).success(function(data){

            // post the snippet to the destination env
            var snippet = data.results[0];

            var url = "https://" + window.location.host +
                "/resources/snippet/" + snippet.path + "?version=" + jobj.ver;

            var code = {
                "queryCode": snippet.query,
                "entityCode": snippet.code,
                "description": snippet.description,
                "queryHost": snippet.queryHost,
                "lang": snippet.lang
            };

            $http({method: 'PUT', withCredentials: true, url: url, data: JSON.stringify(code)}).success(function(){
                $scope.$emit("upReloadSnippets");
                $scope.showExportResult({ok:true, msg: "The resource [ /v" + jobj.ver + "/" + snippet.path + " ] was promoted successfully"});
            }).error(function(data, status, headers, config){
                $scope.showExportResult({ok:false, msg: ((data.code) ? data.code  : status) + " : " + ((data.message) ? data.message :  data)});
            })

        }).error(function(data, status){
            if (status > 0) {
                $scope.showExportResult(((data.code) ? data.code  : status) + " : " + ((data.message) ? data.message :  data));
            }
            else {
                $scope.showExportResult("Connection to the api host was refused");
            }
        });

        $cookies.ExportResult = ""
    }

    //Idle.watch();
*/
    /*$scope.fullPath = function(){
        if (!$scope.snippet.path) return "None selected";
        var v = $scope.snippet.version > 0 ? "v/" + $scope.snippet.version + "/" : "";
        return "https://" + window.location.host + "/bespoke/" + v + $scope.snippet.path;
    };*/

        //$scope.accessToken = true;
        //$scope.isSnippetSelected = true;
        //$scope.isSnippetSelected = function(){
        //return (!!$scope.snippet) && (!!$scope.snippet.path)
    //};
    /*
    $scope.$on("upOpenResource", function(ev, message){
        $scope.snippet = message;

        $scope.isOpen = [0, 1, 0, 0];
        $scope.$broadcast("downOpenResource", message);
    });

    $scope.$on("deselectSnippet", function(ev, message){
        $scope.snippet = {};
        $scope.isOpen = [1, 0, 0, 0];
        $scope.$broadcast("downOpenResource", {});
    });

    $scope.$on("upReloadSnippets", function(ev, message){
        $scope.$broadcast("downReloadSnippets", {})
    });

    $scope.logout = function(){
        //window.location = "/logout"
    };


    $scope.$on('IdleStart', function() {
        $log.debug('IdleStart');
    });

    $scope.$on('IdleEnd', function() {
        $log.debug('IdleEnd');
    });

    $scope.$on('IdleTimeout', function() {
        $log.debug('IdleTimeout');
        //window.location.href = "/logout"
    }); */

}]);

app.controller('CreateEditResourceCtrl', function($scope, $modalInstance, newSnippet){

    $scope.newSnippet = newSnippet;
    $scope.isEditMode = newSnippet.isEditMode;

    $scope.isInvalidValidPath = function(){
        return !$scope.newSnippet.path
    };

    $scope.ok = function(){
        $modalInstance.close($scope.newSnippet);
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

app.controller('ResNameModalCtrl', function($scope, $modalInstance){

    $scope.path = "" ;

    $scope.isInvalidValidPath = function(){
        return !$scope.path
    };

    $scope.ok = function(){
        $modalInstance.close($scope.path);
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
});

app.controller('InfoCtrl', function($scope, $modalInstance, header, message){

    $scope.modalHeader = header;
    $scope.modalMessage = message;

    $scope.ok = function(){
        $modalInstance.close();
    };
});


app.controller('ExportSourceCtrl', ['$scope', '$modalInstance', 'configLoader', function($scope, $modalInstance, configLoader){

    $scope.exports = [];
    $scope.config = undefined;
    $scope.exportAvailable = false;
    $scope.export = {};
    $scope.export.selected = undefined;

    configLoader().then(function(cfg){
        $scope.config = cfg.lookup("promotion");
        $scope.exports =  Object.keys($scope.config).map(function(k) { return { key: k, val: $scope.config[k] }});
    });

    $scope.isExportDisabled = function(){
        return !$scope.exportAvailable;
    };

    $scope.destinationChanged = function(){
        $scope.exportAvailable = true;
    };

    $scope.ok = function(){
        $modalInstance.close($scope.export.selected);
    };

    $scope.cancel = function(){
        $modalInstance.dismiss('cancel');
    };
}]);


app.config(function(IdleProvider, KeepaliveProvider) {
    //IdleProvider.idle(GLOBAL_ENV_CONFIG.timeOut);
    //IdleProvider.timeout(5);
    //KeepaliveProvider.interval(10);
});

