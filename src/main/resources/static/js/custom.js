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

function triggerAreaLink(){
    $("[data-js='triggerAreaLink']").unbind();
    $("[data-js='triggerAreaLink']").click(function () {
        var input = $(this);
        var target = input.attr("data-js-target");
        $(target).html("...");
        loadAreaFn($(target), true);
        $("#modal").modal('hide');
    });
}
function triggerNameChange(){
    $("[data-js='triggerNameChange']").each(function () {
        var input = $(this);
        var name = $('#username').val();
        $('#account-username').text(name)
        input.remove();
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

function networkGraph() {
    if ($("[data-js='networkArea']").length) {
        var area = $(this);

        var jsonData = graphData();
        var container = document.getElementById('network-graph')
        var included = area.find("#network-included");
        var excluded = area.find("#network-excluded");
        var includedTemplate = area.find("#network-included-template");
        var excludedTemplate = area.find("#network-excluded-template");

        var groupDict = {
            0: "#00FF00",
            1: "#8DFF00",
            2: "#8DFF00",
            3: "#E5FF00",
            4: "#FFF600",
            5: "#FFE400",
            6: "#FFAF00",
            7: "#FF9E00",
            8: "#FF7B00",
            9: "#FF5700"
        };

        nodes = new vis.DataSet();
        edges = new vis.DataSet();

        var data = {
            nodes: nodes,
            edges: edges
        };

        var options = {};

        function addNode(id, name, colour) {
            try {
                nodes.add({
                    id: id,
                    label: name,
                    color: colour
                });
            }
            catch (err) {
                console.log(err);
            }
        }

        function addEdge(id, from, to, colour) {
            try {
                edges.add({
                    id: id,
                    from: from,
                    to: to,
                    color: colour
                });
            }
            catch (err) {
                console.log(err);
            }
        }

        for(var i = 0; i < jsonData.length; i++) {
            var node = jsonData[i];
            addNode(node.id, node.name, groupDict[node.scoreGroup]);
        }

        for(var i = 0; i < jsonData.length; i++) {
            var node = jsonData[i];

            for(var j = 0; j < node.matches.length; j++) {
                var node2 = node.matches[j];
                addEdge(i+j+node.id+node2.id, node.id, node2.id, groupDict[node2.scoreGroup]);
            }
        }

        network = new vis.Network(container, data, options);

    }
}

function formSubmit(){
    $("[data-js='form']").unbind();
    $("[data-js='form']").submit(function () {
        console.log("submitting form");

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
            if (xhr.getResponseHeader("sherlock-url") != url || result.includes("<link ")) {
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
    triggerAreaLink();
    triggerNameChange();
    networkGraph();
}

$(function () {
    loadAreas();
    bindPage();
    $('form[data-js="autoSubmit"]').submit();
    // $("select[data-js='select']").trigger('change');
});