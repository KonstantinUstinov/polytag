function SelectSnippet($scope, $http, $modal, configLoader, auth, Upload){

    $scope.client = "";
    $scope.config = undefined;

    configLoader().then(function(cfg){
        $scope.config = cfg;
    });

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

    // only for selected item
    $scope.snippet = {
        path: "",
        version: 0
    };

    $scope.$watch('files', function () {
        $scope.upload($scope.files);
    });

    // for create item
    $scope.snippets = [];

    $scope.lineStatus = function(item){
        if ($scope.isSelected(item)) {
            return {success: true}
        } else if (item.deprecated) return {warning: true};
        else if (item.quarantined) return {danger: true};
        else return {};
    };

    $scope.itemStatus = function(item){
        if (item.deprecated) return "deprecated";
        else if (item.quarantined) return "quarantined";
        else return "";
    };

    $scope.isSelected = function(item){
        return item.path == $scope.snippet.path && item.version == $scope.snippet.version
    };

    $scope.onItemClick = function(item){
        $scope.$emit("upOpenResource", item)
    };

    $scope.$on("downOpenResource", function(ev, message){
        $scope.snippet = message;
    });

    $scope.$on("downReloadSnippets", function(ev, message){
        $scope.refreshData();
    });

    $scope.refreshData = function(){
        if (!$scope.client) return;

        var url = "/resources/owned";
        $http({method: 'GET', withCredentials: true, url: $scope.conf.prefix + url, data: ""}).success(function(data){
            $scope.snippets = data.results
        }).error(function(data, status){
            if (status > 0) {
                $scope.addDangerAlert(data.code + " : " + data.message)
            }
            else {
                $scope.addDangerAlert("Connection to the api host was refused")
            }
        });
    };

    $scope.isExportDisabled = function(item){
        var ecf = $scope.config.lookup("promotion");
        return item.version == 0 || Object.keys(ecf).length == 0;
    };

    $scope.onExport = function(item){
        $scope.resetAlerts();
        var modalInstance = $modal.open({
            templateUrl: 'templates/ExportSource.html',
            controller: 'ExportSourceCtrl',
            resolve: {}
        });

        modalInstance.result.then(function(editedItem){
            var url = editedItem.val + "/resources/promote" +
                "?token=" + auth.getStoredAuthorizationToken() +
                "&destUrl=" + $scope.conf.prefix +
                "&path=" + item.path +
                "&version=" + item.version;
            var win = window.open(url, '_blank');
            win.focus();
        }, function(){
            // on cancel
        });

    };

    $scope.onDownLoad = function(item){
        var url = $scope.conf.prefix + "/resources/download/" + item.version + "/" + item.path;
        window.location = url;
    };

    $scope.openModal = function(isEditMode, item){
        $scope.resetAlerts();
        var dml = $scope.config.lookup("snippetDescMaxLength");
        var pml = $scope.config.lookup("snippetPathMaxLength");

        var modalInstance = $modal.open({
            templateUrl: 'templates/EditModalContent.html',
            controller: 'CreateEditResourceCtrl',
            resolve: {
                newSnippet: function(){

                    return isEditMode ?
                    {
                        isEditMode: true,
                        item: item,
                        path: item.path,
                        description: item.description,
                        snippetDescMaxLength: dml,
                        snippetPathMaxLength: pml
                    }
                        :
                    {
                        isEditMode: false,
                        path: "",
                        description: "",
                        snippetDescMaxLength: dml,
                        snippetPathMaxLength: pml
                    }
                }
            }
        });

        modalInstance.result.then(function(editedItem){
            if (editedItem.isEditMode) {
                editedItem.item.description = editedItem.description;
                $scope.updateDescription(editedItem.item, editedItem.description);
            } else {
                $scope.onCreate(editedItem);
            }
        }, function(){
            // on cancel
        });
    };

    $scope.onCreate = function(item){
        $scope.resetAlerts();

        var code = {
            "description": item.description
        };

        var url = "/resources/snippet/" + item.path;

        $http({
            method: 'PUT', withCredentials: true,
            url: $scope.conf.prefix + url,
            data: JSON.stringify(code)
        }).success(function(data){
            $scope.refreshData();
            $scope.$emit("upReloadSnippets");
            $scope.$emit("deselectSnippet");

        }).error(function(data, status){
            if (status > 0) {
                $scope.addDangerAlert(status + " : " + data.description)
            }
            else {
                $scope.addDangerAlert("Connection to the api is not available")
            }
        });
    };

    $scope.updateDescription = function(item, description){
        $scope.resetAlerts();

        var url = $scope.conf.prefix + "/resources/description/snippet/" + item.version + "/" + item.path;

        $http({method: 'PUT', withCredentials: true, url: url, data: description}).success(function(data){
            $scope.refreshData();
            $scope.$emit("upReloadSnippets");
        }).error(function(data, status){
            if (status > 0) {
                $scope.addDangerAlert(status + " : " + data.message)
            }
            else {
                $scope.addDangerAlert("Connection to the api is not available")
            }
        });
    };

    $scope.onDelete = function(item){
        $scope.resetAlerts();

        var modalInstance = $modal.open({
            templateUrl: 'templates/YesNoContent.html',
            controller: 'YesNoModalCtrl',
            resolve: {
                header: function(){
                    return "Are you sure?"
                },
                message: function(){
                    return "Remove the resource:" + item.path + ", version:" + item.version
                }
            }
        });

        modalInstance.result.then(function(){
            var url = $scope.conf.prefix + "/resources/snippet/" + item.version + "/" + item.path;

            $http({method: 'DELETE', withCredentials: true, url: url, data: ""}).success(function(data){
                $scope.refreshData();
                $scope.$emit("upReloadSnippets");
                $scope.$emit("deselectSnippet", {});
            }).error(function(data, status){
                $scope.addDangerAlert(data.code + " : " + data.message)
            });
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

    $scope.addExample = function(){

        var modalInstance = $modal.open({
            templateUrl: 'templates/ResNameModalContent.html',
            controller: 'ResNameModalCtrl'
        });

        modalInstance.result.then(function(basePath){
            var url = $scope.conf.prefix + "/resources/examples?basePath=" + basePath;

            $http({method: 'PUT', withCredentials: true, url: url}).success(function(data){
                $scope.refreshData();

            }).error(function(data, status){
                if (status > 0) {
                    $scope.addDangerAlert(data.description ? data.description : (data.code + " : " + data.message))
                }
                else {
                    $scope.addDangerAlert("Connection to the api is not available")
                }
            });
        }, function(){
            // on cancel
        });
    };

    $scope.upload = function (files) {
        $scope.resetAlerts();
        if (files && files.length) {
            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                Upload.upload({
                    url: '../resources/download',
                    withCredentials: true,
                    file: file
                }).progress(function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
                }).success(function (data, status, headers, config) {
                    console.log('file ' + config.file.name + ' uploaded. Response: ' + data);
                    $scope.refreshData();
                }).error(function(data, status){
                    if (status > 0) {
                        $scope.addDangerAlert(data.description ? data.description : (data.code + " : " + data.message))
                    }
                    else {
                        $scope.addDangerAlert("Connection to the api is not available")
                    }
                });
            }
        }
    };
}

