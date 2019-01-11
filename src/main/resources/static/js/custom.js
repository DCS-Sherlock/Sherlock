//Sherlock JS
function modal(){
    $("a[data-js='modal']").unbind();
    $("a[data-js='modal']").click(function () {
        var input = $(this);
        var link = input.attr("href");

        input.prop("disabled", true);
        input.addClass("disabled");

        $.ajax({
            type: "GET",
            accept:"text/html",
            dataType: "html",
            url: link,
            timeout: 30000,
            data: {
                "ajax": true
            },
            success: function(result, status, xhr) {
                if (xhr.getResponseHeader("sherlock-url") != link || result.includes("</script>")) {
                    window.location = xhr.getResponseHeader("sherlock-url");
                } else {
                    $("#modal").html(result);
                    $("#modal").modal('show');
                    loadPage();
                }
            },
            error: function (xhr, ajaxOptions, thrownError) {
                //Todo proper error messages
                console.log("error");
                console.log(xhr.status);
                console.log(thrownError);
                window.location = xhr.getResponseHeader("sherlock-url");
            }
        });

        input.prop("disabled", false);
        input.removeClass("disabled");

        return false;
    });
}

function area(){
    $("div[data-js='area']").each(function () {
        var input = $(this);
        var link = input.attr("data-js-href");

        $.ajax({
            type: "GET",
            accept:"text/html",
            dataType: "html",
            url: link,
            timeout: 30000,
            data: {
                "ajax": true
            },
            success: function(result, status, xhr) {
                if (xhr.getResponseHeader("sherlock-url") != link || result.includes("</script>")) {
                    window.location = xhr.getResponseHeader("sherlock-url");
                } else {
                    input.html(result);
                    loadPage();
                }
            },
            error: function (xhr, ajaxOptions, thrownError) {
                //Todo proper error messages
                console.log("error");
                console.log(xhr.status);
                console.log(thrownError);
                window.location = xhr.getResponseHeader("sherlock-url");
            }
        });
    });
}

function form(){
    $("form[data-js='form']").unbind();
    $("form[data-js='form']").submit(function () {
        var input = $(this);
        var link = input.attr("action");
        var target = $(input.attr("data-js-target"));

        input.find("button[type=submit]").prop("disabled", true);
        input.find("button[type=submit]").addClass("disabled");

        var data = new FormData(this);
        data.append("ajax", true);

        $.ajax({
            type: "POST",
            accept:"text/html",
            dataType: "html",
            url: link,
            timeout: 30000,
            cache: false,
            contentType: false,
            processData: false,
            data: data,
            success: function(result, status, xhr) {
                if (xhr.getResponseHeader("sherlock-url") != link || result.includes("</script>")) {
                    window.location = xhr.getResponseHeader("sherlock-url");
                } else {
                    target.html(result);
                }
                loadPage();
            },
            error: function (xhr, ajaxOptions, thrownError) {
                //Todo proper error messages
                console.log("error");
                console.log(xhr.status);
                console.log(thrownError);
                window.location = xhr.getResponseHeader("sherlock-url");
            }
        });

        return false;
    });
}

function hideClose() {
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

function loadPage() {
    modal();
    form();
    hideClose();
}

$(function () {
    area();
    loadPage();
});

// $(function () {
//     $("[data-js=tab]").click(function () {
//         var input = $(this);
//         $(input.attr("data-js-target")).tab('show')
//         return false;
//     });
// });