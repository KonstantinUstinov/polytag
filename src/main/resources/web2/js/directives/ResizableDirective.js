function ResizableDirective(){
    return {
        restrict: 'A',
        scope: {
            callback: '&onResize'
        },
        link: function postLink(scope, elem, attrs){

            elem.addClass("resizable")
                .wrap('<div/>')
                .css({'overflow': 'hidden'})
                .parent()
                .css({
                    'display': 'inline-block',
                    'overflow': 'hidden',
                    'height': function(){
                        return $('.resizable', this).height();
                    },
                    'width': function(){
                        return $('.resizable', this).width();
                    },
                    'paddingBottom': '12px',
                    'paddingRight': '12px'

                }).resizable({
                    helper: "ui-resizable-helper"
                })
                .on('resizestop', function(){
                    if (scope.callback) scope.callback();
                })
                .find('.resizable')
                .css({
                    overflow: 'auto',
                    width: '100%',
                    height: '100%'
                });
        }
    };
}