//Sherlock JS
function loadAreas(){
    $("[data-js='area']").each(function () {
        var input = $(this);
        loadAreaFn(input);
    });
}
function triggerAreas(){
    $("[data-js='triggerArea']").each(function () {
        var input = $(this);
        var target = input.attr("data-js-target");
        input.remove();
        $(target).html("...");
        loadAreaFn($(target), true);
    });
}

function loadAreaFn(input){
    var url = input.attr("data-js-href");

    getAjax(
        url,
        function(result, status, xhr) {
            input.html(result);
        }
    );
}

function modalLinks(){
    $("[data-js='modal']").unbind();
    $("[data-js='modal']").click(function () {
        var input = $(this);
        var url = input.attr("href");

        input.prop("disabled", true);
        input.addClass("disabled");

        getAjax(
            url,
            function(result, status, xhr) {
                $("#modal").html(result);
                $("#modal").modal('show');
                selectAreas();
                // $("select[data-js='select']").trigger('change');
            }
        );

        input.prop("disabled", false);
        input.removeClass("disabled");

        return false;
    });
}

function selectAreas(){
    $("select[data-js='select']").unbind();
    $("select[data-js='select']").on('change', function () {
        var input = $(this);
        var value =  input.val();
        var target = input.attr("data-js-target");
        var url = input.attr("data-js-href");

        $(target).html("...");

        getAjax(
            url + value,
            function(result, status, xhr) {
                $(target).html(result);
            }
        );

        return false;
    });
}

function formSubmitButton() {
    $("[data-js='formSubmit']").unbind();
    $("[data-js='formSubmit']").each(function() {
        var input = $(this);
        var target = input.attr("data-js-target");
        if($(target).length == 0) {
            $(this).addClass('d-none');
        } else {
            $(this).removeClass('d-none');
        }
    });
    $("button[data-js='formSubmit']").click(function() {
        var input = $(this);
        var target = input.attr("data-js-target");
        $(target).submit();
    });
}

function formSubmit(){
    $("[data-js='form']").unbind();
    $("[data-js='form']").submit(function () {
        var input = $(this);
        var url = input.attr("action");
        var target = $(input.attr("data-js-target"));

        input.find("button[type=submit]").prop("disabled", true);
        input.find("button[type=submit]").addClass("disabled");

        var data = new FormData(this);
        data.append("ajax", "true");

        submitAjax(
            url,
            data,
            function(result, status, xhr) {
                target.html(result);
            },
            "POST"
        );

        return false;
    });
}

function hideCloseButtons() {
    $( ".js-cancel" ).each(function() {
        if ($(this).closest("#modal-container").length == 1) {
            if ($(this).is("button")) {
                $(this).removeClass('d-none');
            } else if ($(this).is("a")) {
                $(this).hide();
            }
        }
    });
}

function getAjax(url, success) {
    var data = {
        ajax: "true"
    };
    submitAjax(url, data, success, "GET");
}

function submitAjax(url, data, success, type) {
    var input = {
        type: type,
        accept:"text/html",
        dataType: "html",
        url: url,
        timeout: 30000,
        data: data,
        success: function (result, status, xhr) {
            if (xhr.getResponseHeader("sherlock-url") != url || result.includes("</script>")) {
                window.location = xhr.getResponseHeader("sherlock-url");
            } else {
                success(result, status, xhr);
            }
            bindPage();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            console.log(thrownError);
            $('#javascript-error').removeClass('d-none');
            $('#javascript-error-message').text(xhr.status);
            $("#modal").modal('hide');
        }
    };

    if (type == "POST") {
        $.extend(input, {
            cache: false,
            contentType: false,
            processData: false
        });
    }

    $.ajax(input);
}

function tooltips() {
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover()
}

function bindPage() {
    tooltips();
    selectAreas();
    modalLinks();
    formSubmit();
    hideCloseButtons();
    formSubmitButton();
    triggerAreas();
}

$(function () {
    loadAreas();
    bindPage();
    $('form[data-js="autoSubmit"]').submit();
    // $("select[data-js='select']").trigger('change');
});