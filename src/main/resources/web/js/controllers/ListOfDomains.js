function ListOfDomains($scope, $http, $modal, configLoader){

    $scope.domains = [];

    $scope.init = function(){
        $scope.refreshData();
    };

    $scope.refreshData = function(){
        var url = "https://" + window.location.host + "/domains";

        $http({method: 'GET', withCredentials: true, url: url}).success(function(data){
            $scope.domains = data
        }).error(function(err){
            $scope.tags = [];
            console.log(err);
        });

    }

    $scope.onDelete = function(item){
        var modalInstance = $modal.open({
            templateUrl: 'templates/YesNoContent.html',
            controller: 'YesNoModalCtrl',
            resolve: {
                header: function(){
                    return "Are you sure?"
                },
                message: function(){
                    return "Remove the doamin: " + item.path + " id: " + item.id;
                }
            }
        });

        modalInstance.result.then(function(){
            var url = "https://" + window.location.host + "/domains/" + item.id;

            $http({method: 'DELETE', withCredentials: true, url: url, data: ""}).success(function(data){
                $scope.refreshData();
            }).error(function(data, status){
                console.log(data);
                console.log(status);
            });
        }, function(){
            // on cancel
        });
    };


    $scope.openModal = function(isEditMode, item){
        var modalInstance = $modal.open({
            templateUrl: 'templates/EditModalDomain.html',
            controller: 'EditModalDomainCtrl',
            resolve: {
                editDomain: function(){
                    return {
                        domainId: item.id,
                        path: item.path
                    }
                }
            }
        });

        modalInstance.result.then(function(editedItem){

            var url = "https://" + window.location.host  + "/domains/" + item.id + "?domain_uri=" + editedItem.path;

            $http({method: 'PUT', withCredentials: true, url: url, data: {}, headers: {"Content-Type": "application/json;charset=UTF-8"},}).success(function(data){
                $scope.refreshData();
            }).error(function(data, status){
                console.log(data);
                console.log(status);
                console.log(url);
            });

            //console.log(editedItem);
            //$scope.refreshData();
        }, function(){
            // on cancel
        });
    };

    $scope.create = function(){
        var modalInstance = $modal.open({
            templateUrl: 'templates/CreateModalDomain.html',
            controller: 'EditModalDomainCtrl',
            resolve: {
                editDomain: function(){
                    return {
                        path: ""
                    }
                }
            }
        });

        modalInstance.result.then(function(editedItem){

            var url = "https://" + window.location.host  + "/domains/?domain_uri=" + editedItem.path;

            $http({method: 'POST', withCredentials: true, url: url, data: {}, headers: {"Content-Type": "application/json;charset=UTF-8"},}).success(function(data){
                $scope.refreshData();
            }).error(function(data, status){
                console.log(data);
                console.log(status);
                console.log(url);
            });

            //console.log(editedItem);
            //$scope.refreshData();
        }, function(){
            // on cancel
        });
    };


}