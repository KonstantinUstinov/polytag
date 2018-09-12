function Logs($scope, $http, $timeout){

    $scope.logs = [];
    $scope.isPollingEnabled = false;
    $scope.pollingTimeout = 5000;
    $scope.logLevel = "ALL";

    $scope.snippet = undefined;

    $scope.toDate = new Date();
    $scope.fromDate = new Date(moment().subtract(1, "day"));

    $scope.toNow = function(){
        $scope.toDate = new Date();
    };

    $timeout(function(){
        if ($scope.isPollingEnabled) {
            $scope.toNow();
            $scope.refreshData();
        }
        $timeout(arguments.callee, $scope.pollingTimeout);
    }, $scope.pollingTimeout);

    $scope.$on("downOpenResource", function(ev, message){
        $scope.snippet = message;
        $scope.logs = [];
        $scope.refreshData();
    });

    //val Warn = "warning"
    //val Error = "error"
    //val Debug = "debug"
    //val Info = "info"
    $scope.statusClass = function(item){
        if (item.level == "error") return {danger: true};
        if (item.level == "warning") return {warning: true};
        return {};
    };

    $scope.refreshData = function(){
        if (!$scope.snippet || !$scope.snippet.path) return;
        var dateFrom = $scope.fromDate.getTime();
        var dateTo = $scope.toDate.getTime();

        var url = "https://" + window.location.host + "/logs" +
            "/" + $scope.snippet.version +
            "/" + $scope.snippet.path +
            "?limit=50" +
            "&dateFrom=" + dateFrom +
            "&dateTo=" + dateTo;

        if ($scope.logLevel != 'ALL') {
            url += '&level=' + $scope.logLevel.toLowerCase();
        }

        $http({method: 'GET', withCredentials: true, url: url}).success(function(data){
            $scope.logs = data.results
        }).error(function(err){

        });

    };

}
