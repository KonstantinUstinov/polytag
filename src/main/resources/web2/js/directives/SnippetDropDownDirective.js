function SnippetDropDownDirective() {
    var data = [
        {id: 0, tag: 'securities/products'},
        {id: 1, tag: 'securities/products/normalized'},
        {id: 2, tag: 'securities/issuers'},
        {id: 3, tag: 'accounts/mg'},
        {id: 4, tag: 'accounts/mcle'},
        {id: 5, tag: 'accounts/account'},
        {id: 6, tag: 'accounts/gfcid'},
        {id: 7, tag: 'corporateactions/corporateAction'},
        {id: 8, tag: 'prices/price'},
        {id: 9, tag: 'ssi/settlementInstruction'}
    ];

    function format(item) {
        return item.tag;
    }

    return {
        restrict: 'A',
        require: '?ngModel',
        link: function (scope, element, attrs, ngModel) {
            var propStr = attrs['snippetdropdown'];
            var propObj = JSON.parse(propStr);

            $(element).select2({
                placeholder: "Select Resource Path",
                width: "100%",
                data: {results: data, text: 'tag'},
                formatSelection: format,
                formatResult: format
            });

            for (var key in propObj) {
                $(element).select2(key, propObj[key])
            }

            ngModel.$render = function () {
                $(element).select2("val", ngModel.$viewValue)
            };

            $(element).on('change', function (e) {
                var txt = data.filter(function (el) {
                    return (el.id + "") === e.val
                })[0].tag;
                scope.$apply(function () {
                    ngModel.$setViewValue(txt);
                })
            })
        }
    }
}
