function ListOfTags($scope, $http, $modal, configLoader){

    $scope.tags = [];

    configLoader().then(function(cfg) {
        $scope.config = cfg;
    });

    $scope.init = function(){
        var date = new Date();
        date.setFullYear( date.getFullYear() - 3 );
        $scope.toCreationDate = new Date();
        $scope.fromCreationDate = date;

        $scope.toUpdateDate = new Date();;
        $scope.fromUpdateDate = date;
        $scope.refreshData();
    };

    $scope.toISODate = function(date) {
        return new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
    };

    $scope.refreshData = function(){
        var toCreationDate = $scope.toISODate($scope.toCreationDate);
        var fromCreationDate = $scope.toISODate($scope.fromCreationDate);

        var toUpdateDate = $scope.toISODate($scope.toUpdateDate);
        var fromUpdateDate = $scope.toISODate($scope.fromUpdateDate);

        var url = "https://" + window.location.host + "/api/tags/search" +
            "?limit=50" +
            "&creationdatefrom=" + fromCreationDate +
            "&creationdateto=" + toCreationDate +
            "&updatedatefrom=" + fromUpdateDate +
            "&updatedateto=" + toUpdateDate;

        if (typeof $scope.dsps != 'undefined' && $scope.dsps != 'ALL') {
            url += '&dsp=' + $scope.dsps;
        }

        if (typeof $scope.polytagid != 'undefined' && $scope.polytagid)
        {
            url += "&polytagid=" + $scope.polytagid;
        }

        if(typeof $scope.playerid != 'undefined' && $scope.playerid)
        {
            url += "&playerid=" + $scope.playerid;
        }

        $http({method: 'GET', withCredentials: true, url: url}).success(function(data){
            $scope.tags = data
        }).error(function(err){
            $scope.tags = [];
            console.log(err);
        });

    };

    $scope.onDelete = function(item){
        var modalInstance = $modal.open({
            templateUrl: 'templates/YesNoContent.html',
            controller: 'YesNoModalCtrl',
            resolve: {
                header: function(){
                    return "Are you sure?"
                },
                message: function(){
                    return "Remove the tag: " + item.name + " id: " + item.id;
                }
            }
        });

        modalInstance.result.then(function(){
            var url = "https://" + window.location.host + "/api/tags/" + item.id;

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

    $scope.onItemClick = function(item){
        $scope.$emit("upOpenResource", item)
    };

    $scope.findDomain = function(polyTag, domains){

        //domains.forEach(function(a){if (polyTag.indexOf(a)>-1) return a;});
        var matches = domains.filter(function(domain){
            if(domain) {
                return polyTag.indexOf(domain) >= 0;
            }
        });

        if (matches.length > 0)
            return matches[0];
        else
            return "https://s.cubiqads.com/api/tags";
    };

    $scope.openModal = function(isEditMode, item){
        var modalInstance = $modal.open({
            templateUrl: 'templates/EditModalTag.html',
            controller: 'EditModalTagCtrl',
            resolve: {
                editTag: function(){
                    return {
                        originalTag: item.originalTag,
                        polyTag: item.polyTag,
                        DSPs: item.DSPs[0],
                        tagId: item.id,
                        domains: $scope.config.domains,
                        domain: $scope.findDomain(item.polyTag, $scope.config.domains)
                    }
                }
            }
        });

        modalInstance.result.then(function(editedItem){
            item.originalTag = editedItem.originalTag;
            item.polyTag = editedItem.polyTag;
            var dspArray=[];
            if (typeof editedItem.DSPs != 'undefined' && editedItem.DSPs)
                if(editedItem.DSPs !== 'NA')
                    dspArray.push(editedItem.DSPs);
            item.DSPs = dspArray;
            console.log(item);
            $scope.updateTag(item);
        }, function(){
            // on cancel
        });
    };

    $scope.updateTag = function(item){

        var url = "https://" + window.location.host  + "/api/tags/" + item.id ;

        var update = {};
        update.polyTag = item.polyTag;
        update.originalTag = item.originalTag;
        update.name = item.name;
        update.playerIDs = item.playerIDs;
        update.DSPs = item.DSPs;

        $http({method: 'PUT', withCredentials: true, url: url, data: update, headers: {"Content-Type": "application/json;charset=UTF-8"},}).success(function(data){
            $scope.refreshData();
        }).error(function(data, status){
            console.log(data);
            console.log(status);
            console.log(url);
        });
    };

    $scope.create = function(){
        var modalInstance = $modal.open({
            templateUrl: 'templates/CreateModalTag.html',
            controller: 'CreateModalTagCtrl',
            resolve: {
                createdTag: function(){
                    return {
                        originalTag: "",
                        name: "",
                        dps: "Nuviad",
                        domains: $scope.config.domains,
                        domain: "https://s.cubiqads.com/api/tags"
                    }
                }
            }
        });

        modalInstance.result.then(function(createItem){
            //console.log(createItem);

            var dspArray=[];
            if (typeof createItem.dps != 'undefined' && createItem.dps)
                dspArray.push(createItem.dps);
            else
                dspArray.push("Nuviad");

            createItem.DSPs = dspArray;

            console.log(createItem);
            var url = "https://" + window.location.host  + "/api/tags/";
            $http({method: 'POST', withCredentials: true, url: url, data: createItem, headers: {"Content-Type": "application/json;charset=UTF-8"},}).success(function(data){
                $scope.refreshData();
            }).error(function(data, status){
                console.log(data);
                console.log(status);
                console.log(url);
            });

        }, function(){

        });
    };

    $scope.toNowCreation = function(){
        $scope.toCreationDate = new Date();
    };

    $scope.toNowUpdate = function() {
        $scope.toUpdateDate = new Date();
    }
}