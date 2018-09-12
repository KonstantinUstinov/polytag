function SelectPickerDirective() {
    return {
        restrict: 'A',
        require: '?ngModel',
        priority: 1,
        link: function (scope, element, attrs, ngModel) {
             $(element).selectpicker();

            if ( !ngModel ) return;

            ngModel.$render = function() {
                $(element).selectpicker("val", ngModel.$viewValue);
                $(element).selectpicker('render');
            };

            $(element).change( function() {
                scope.$apply(function(){
                    var data = $(element).selectpicker("val");
                    ngModel.$setViewValue(data);
                })
            })

        }
    }
}
