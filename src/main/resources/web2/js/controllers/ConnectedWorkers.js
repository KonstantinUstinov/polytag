function ConnectedWorkers($scope, $http, $modal, $timeout, configLoader, auth, parsingService) {

    $scope.client = "";
    $scope.config = undefined;
    $scope.workers = [];
    $scope.isPollingEnabled = true;
    $scope.pollingTimeout = 5000;
    $scope.balance = 0;

    configLoader().then(function(cfg){
        $scope.config = cfg;
    });

    $timeout(function() {
        if ($scope.isPollingEnabled) {
            $scope.refreshData();
        }
        $timeout(arguments.callee, $scope.pollingTimeout);
    }, $scope.pollingTimeout);

    $scope.$on("clientNameChanged", function(ev, message){
        $scope.init(message.clientName);
    });

    $scope.conf = {
        prefix: "https://" + window.location.host
    };

    $scope.init = function(clientName){
        $scope.client = clientName;
        $scope.refreshData();
    };

    $scope.$on("downReloadWorkers", function(ev, message){
        $scope.refreshData();
    });

    $scope.refreshData = function(){
        if (!$scope.client) return;

        $scope.resetAlerts();

        var url = "/cluster/status";
        $http({method: 'GET', withCredentials: true, url: $scope.conf.prefix + url, data: ""}).success(function(data){
            $scope.workers = data.results;

            var url = "/clients";
            $http({method: 'GET', withCredentials: true, url: $scope.conf.prefix + url, data: ""}).success(function(data) {
                $scope.balance = -$scope.workers.length;
                $(data.results).each(function() {
                    $scope.balance += this.numWorkers;
                });
            }).error(function(data, status) {
                if (status > 0) {
                    $scope.addDangerAlert(data.code + " : " + data.message);
                }
                else {
                    $scope.addDangerAlert("Connection to the api host was refused");
                }
            });

        }).error(function(data, status){
            if (status > 0) {
                $scope.addDangerAlert(data.code + " : " + data.message);
            }
            else {
                $scope.addDangerAlert("Connection to the api host was refused");
            }
        });
    };

    $scope.openModal = function(item){
        $scope.resetAlerts();
        item = item || { authority: { host: "localhost", port: "" } };

        var modalInstance = $modal.open({
            templateUrl: 'templates/WorkerModalContent.html',
            controller: 'WorkerController',
            resolve: {
                newWorker: function(){

                    return {
                        isEditMode: false,
                        host: item.authority.host,
                        port: item.authority.port
                    };
                }
            }
        });

        modalInstance.result.then(function(editedItem){
            $scope.onCreate(editedItem);
        }, function(){
            // on cancel
        });
    };

    $scope.onCreate = function(item){
        $scope.resetAlerts();
        $scope.defaultTags = item.tags;

        var headers = {
            "Content-Type": "application/x-www-form-urlencoded"
        };

        function spawn(port, tags) {
            var url = $scope.conf.prefix + "/cluster/worker/localhost/" + port;

            $http({
                method: 'POST',
                withCredentials: true,
                url: url,
                data: $.param({"tags": tags}),
                headers: headers
            }).success(function(data){
                // success
            }).error(function(data, status){
                if (status > 0) {
                    $scope.addDangerAlert(status + " : " + data.message)
                }
                else {
                    $scope.addDangerAlert("Connection to the api is not available")
                }
            });

        }

        var ports = parsingService.parsePorts(item.port);
        if (ports.length > 1) {

            var modalInstance = $modal.open({
                templateUrl: 'templates/YesNoContent.html',
                controller: 'YesNoModalCtrl',
                resolve: {
                    header: function(){
                        return "Are you sure?"
                    },
                    message: function(){
                        return "This operation will spawn " + ports.length + " workers.";
                    }
                }
            });

            modalInstance.result.then(function(){
                for (var index = 0; index < ports.length; index++) {
                    spawn(ports[index], item.tags);
                }
                $scope.refreshData();
                $scope.$emit("upReloadWorkers");
            }, function(){
                $scope.openModal(false, item);
            });

        }
        else {
            spawn(ports[0], item.tags);
            $scope.refreshData();
            $scope.$emit("upReloadWorkers");
        }
    };

    $scope.onStop = function(item) {
        $scope.resetAlerts();

        var modalInstance = $modal.open({
            templateUrl: 'templates/YesNoContent.html',
            controller: 'YesNoModalCtrl',
            resolve: {
                header: function(){
                    return "Are you sure?"
                },
                message: function(){
                    return "Worker " + item.authority.host + ":" + item.authority.port + " will be stopped."
                }
            }
        });

        modalInstance.result.then(function(){
            var url = $scope.conf.prefix + "/cluster/worker/" + item.authority.host + "/" + item.authority.port;

            $http({method: 'DELETE', withCredentials: true, url: url, data: ""}).success(function(data){
                $scope.refreshData();
                $scope.$emit("upReloadWorkers");
            }).error(function(data, status){
                $scope.addDangerAlert(data.code + " : " + data.message)
            });
        }, function(){
            // on cancel
        });
    };

    $scope.stopAll = function() {
        $scope.resetAlerts();

        var modalInstance = $modal.open({
            templateUrl: 'templates/YesNoContent.html',
            controller: 'YesNoModalCtrl',
            resolve: {
                header: function(){
                    return "Are you sure?"
                },
                message: function(){
                    return "All workers will be stopped."
                }
            }
        });

        modalInstance.result.then(function(){
            for (var index = 0; index < $scope.workers.length; index++) {
                var worker = $scope.workers[index];
                var url = $scope.conf.prefix + "/cluster/worker/" + worker.authority.host + "/" + worker.authority.port;

                $http({method: 'DELETE', withCredentials: true, url: url, data: ""}).success(function(data){
                    // success
                }).error(function(data, status){
                    $scope.addDangerAlert(data.code + " : " + data.message)
                });
                $scope.refreshData();
                $scope.$emit("upReloadWorkers");
            }
        }, function(){
            // on cancel
        });
    };

    // alerts

    $scope.resetAlerts = function(){
        $scope.alerts = [];
    };

    $scope.resetAlerts();

    $scope.addDangerAlert = function(msg){
        $scope.alerts.push({type: "danger", msg: msg});
    };

    $scope.addSuccessAlert = function(msg){
        $scope.alerts.push({type: "success", msg: msg});
    };

    $scope.closeAlert = function(index){
        $scope.alerts.splice(index, 1);
    };

}

