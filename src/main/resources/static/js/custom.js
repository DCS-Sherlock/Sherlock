//Sherlock JS
$(function () {
    $("[data-js=tab]").click(function () {
        var input = $(this);
        $(input.attr("data-js-target")).tab('show')
        return false;
    });
});