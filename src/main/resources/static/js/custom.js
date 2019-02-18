//Sherlock Javascript
loadingHTML = '<img src="/img/load.gif" class="mx-auto d-block" height="250px">';

function loadAreaAjax(input){
    getAjax(
        input.attr("data-js-href"),
        function(result, status, xhr) {
            input.html(result);
        }
    );
}

function loadArea() {
    $("[data-js='area']").each(function () {
        var input = $(this);
        loadAreaAjax(input);
    });
}

function loadAreaTrigger() {
    $("[data-js='triggerArea']").each(function () {
        var input = $(this);
        var target = input.attr("data-js-target");

        input.remove();
        $(target).html(loadingHTML);
        loadAreaAjax($(target));
    });
}

function loadAreaLink() {
    $("[data-js='triggerAreaLink']").unbind();
    $("[data-js='triggerAreaLink']").click(function () {
        var input = $(this);
        var target = input.attr("data-js-target");

        $(target).html(loadingHTML);
        loadAreaAjax($(target));
        $("#modal").modal('hide');
    });
}

function loadAreaInputTrigger(){
    $("select[data-js='select']").unbind();
    $("select[data-js='select']").on('change', function () {
        var input = $(this);
        var value =  input.val();
        var target = input.attr("data-js-target");
        var url = input.attr("data-js-href");

        $(target).html(loadingHTML);

        getAjax(
            url + value,
            function(result, status, xhr) {
                $(target).html(result);
            }
        );

        return false;
    });
}

function usernameChange(){
    $("[data-js='triggerNameChange']").each(function () {
        var input = $(this);

        input.remove();
        var name = $('#username').val();
        $('#account-username').text(name);
    });
}

function modalLink(){
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
                loadAreaInputTrigger();
            }
        );

        input.prop("disabled", false);
        input.removeClass("disabled");

        return false;
    });
}

function loadNetworkGraph() {
    if ($("[data-js='networkArea']").length) {
        var includedArea = $("#network-included");
        var includedTemplate = $("#network-included-template");
        var includedEmptyTemplate = $("#network-included-template-empty");

        var excludedArea = $("#network-excluded");
        var excludedTemplate = $("#network-excluded-template");
        var excludedEmptyTemplate = $("#network-excluded-template-empty");

        var colours = {
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

        var container = document.getElementById('network-graph');

        var nodes = new vis.DataSet();
        var edges = new vis.DataSet();

        var data = {
            nodes: nodes,
            edges: edges
        };

        var options = {};

        var network = new vis.Network(container, data, options);

        var json = graphData();
        var dataURL = "";

        function addNode(id, name, colour) {
            try {
                nodes.add({
                    id: id,
                    label: name,
                    color: colour
                });
            }
            catch (err) { }
        }

        function addEdge(from, to, colour) {
            var id = Math.min(from, to) + "_" + Math.max(from, to);

            try {
                edges.add({
                    id: id,
                    from: Math.min(from, to),
                    to: Math.max(from, to),
                    color: colour
                });
            }
            catch (err) { }
        }

        function addMatches(node) {
            for(var j = 0; j < node.matches.length; j++) {
                var match = node.matches[j];
                addEdge(node.id, match.id, colours[match.scoreGroup]);
            }
        }

        function clickEvent() {
            var edgeDelete = $("[data-js='edgeDelete']");
            if (network.getSelectedEdges().length == 0) {
                edgeDelete.prop("disabled", true);
                edgeDelete.addClass("disabled");
            } else {
                edgeDelete.prop("disabled", false);
                edgeDelete.removeClass("disabled");
            }

            var nodeMatches = $("[data-js='nodeMatches']");
            var nodeDelete = $("[data-js='nodeDelete']");
            if (network.getSelectedNodes().length == 0) {
                nodeMatches.prop("disabled", true);
                nodeMatches.addClass("disabled");
                nodeDelete.prop("disabled", true);
                nodeDelete.addClass("disabled");
            } else {
                nodeMatches.prop("disabled", false);
                nodeMatches.removeClass("disabled");
                nodeDelete.prop("disabled", false);
                nodeDelete.removeClass("disabled");
            }
        }

        function submissionMatchesEvent(target) {
            var result = $.grep(json, function( n, i ) {
                return n.id == target;
            });

            if (result.length == 1) {
                var node = result[0];
                for(var j = 0; j < node.matches.length; j++) {
                    var match = node.matches[j];
                    var matchNode = $.grep(json, function( n, i ) {
                        return n.id == match.id;
                    });
                    if (matchNode.length == 1) {
                        var node2 = matchNode[0];
                        addNode(node2.id, node2.name, colours[node2.scoreGroup]);
                        addMatches(node2);
                    }
                }
            }

            update();
        }

        function bindEvents() {
            $("[data-js='submissionAdd']").unbind();
            $("[data-js='submissionAdd']").click(function () {
                var input = $(this);
                var target = input.attr("data-js-target");

                var result = $.grep(json, function( n, i ) {
                    return n.id == target;
                });

                if (result.length == 1) {
                    var node = result[0];
                    addNode(node.id, node.name, colours[node.scoreGroup]);
                    addMatches(node);
                }

                update();
            });

            $("[data-js='submissionMatches']").unbind();
            $("[data-js='submissionMatches']").click(function () {
                var input = $(this);
                var target = input.attr("data-js-target");
                submissionMatchesEvent(target);
            });

            $("[data-js='submissionDelete']").unbind();
            $("[data-js='submissionDelete']").click(function () {
                nodes.remove({id: $(this).attr("data-js-target")});
                update();
            });
        }

        function update() {
            includedArea.html("");
            excludedArea.html("");

            var visible = 0;
            var invisible = 0;

            for(var i = 0; i < json.length; i++) {
                var submission = json[i];
                var node = nodes.get(submission.id);

                if (node == null) { //Not visible on graph
                    var copy = excludedTemplate.clone();
                    copy.find(".js-name").text(submission.name);
                    copy.find(".js-id").attr("data-js-target", submission.id);
                    excludedArea.append(copy.html());

                    invisible++;
                } else { //Visible in graph
                    var copy = includedTemplate.clone();
                    copy.find(".js-name").text(submission.name);
                    copy.find(".js-id").attr("data-js-target", submission.id);
                    includedArea.append(copy.html());

                    visible++;
                }
            }

            if (visible == 0) {
                includedArea.html(includedEmptyTemplate.html());
            }

            if (invisible == 0) {
                excludedArea.html(excludedEmptyTemplate.html());
            }

            bindEvents();
            clickEvent();
        }

        network.on('click', function(properties) {
            clickEvent();
        });

        network.on("afterDrawing", function (ctx) {
            dataURL = ctx.canvas.toDataURL();
        });


        $("[data-js='edgeDelete']").click(function () {
            $(this).blur();

            var selectedEdges = network.getSelectedEdges();
            for (var i in selectedEdges) {
                edges.remove(selectedEdges[i]);
            }

            update();
        });

        $("[data-js='nodeDelete']").click(function () {
            $(this).blur();

            var selectedNodes = network.getSelectedNodes();
            for (var i in selectedNodes) {
                nodes.remove(selectedNodes[i]);
            }

            update();
        });

        $("[data-js='nodeMatches']").click(function () {
            $(this).blur();

            var selectedNodes = network.getSelectedNodes();
            for (var i in selectedNodes) {
                submissionMatchesEvent(selectedNodes[i]);
            }
        });

        $("[data-js='graphDownload']").click(function () {
            var input = $(this);
            input.blur();
            input.attr("href", dataURL);
        });

        update();
    }
}

function submitForm(){
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

function displayModalLinks() {
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
            rebindEvents();
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

function displayTooltips() {
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover()
}

function rebindEvents() {
    displayTooltips();
    loadAreaInputTrigger();
    modalLink();
    submitForm();
    displayModalLinks();
    // formSubmitButton();
    loadAreaTrigger();
    loadAreaLink();
    usernameChange();
    loadNetworkGraph();
}

$(function () {
    loadArea();
    rebindEvents();
    $('form[data-js="autoSubmit"]').submit();
});