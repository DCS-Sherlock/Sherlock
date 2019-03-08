/*
    SHERLOCK JS
 */

/**
 * The HTML content to replace a div with when reloading the content
 * @type {string}
 */
loadingHTML = '<img src="/img/load.gif" class="mx-auto d-block" height="75px">';

/**
 * Performs multiple functions on the compare and report pages:
 * - Changes the colour of the highlighted lines according to the match colour
 * - If you click on a highlighted line, it'll display the details for that match
 * - If you click "view" on a match, it'll display the details for that match
 * - Toggles the visibility of the matches table
 *
 * Please Note: "pointer-events: none" must be removed from the .line-highlight
 * class in prism.css if PrismJS is updated!
 */
function submissionResultsPage() {
    //Only run on the compare submissions page
    if ($("#compare-data").length || $("#report-data").length) {
        //Fetch the match details
        var matches = getMatchesJSON();
        var lineMap = getMapJSON();

        //Get the workspace id and submission id on the report page
        var workspaceId = 0;
        var submissionId = 0;
        if ($("#report-data").length) {
            workspaceId = getWorkspaceId();
            submissionId = getSubmissionId();
        }

        var active = -1; //Which match is active
        var loadingReport = false; //Whether the page is loading, or a match on the report page

        /**
         * Converts a HEX colour code to a RGBA colour code with transparency
         *
         * @param colour the hex colour code to convert
         * @returns {string} the rgba colour code equivalent
         */
        function rgba(colour) {
            return 'rgba(' + parseInt(colour.slice(-6,-4),16)
                + ',' + parseInt(colour.slice(-4,-2),16)
                + ',' + parseInt(colour.slice(-2),16)
                +',0.4)';
        }

        /**
         * Converts the RGBA colour code to CSS linear gradient settings
         *
         * @param colour the colour to convert
         *
         * @returns {{background: string}} the CSS settings
         */
        function gradient(colour) {
            var right = rgba("#2d2d2d");

            return {
                'background': colour,
                'background': "-moz-linear-gradient(90deg, " + colour + " 0%, " + right + " 100%)",
                'background': "-webkit-linear-gradient(90deg, " + colour + " 0%, " + right + " 100%)",
                'background': "linear-gradient(90deg, " + colour + " 0%, " + right + " 100%)"
            }
        }

        /**
         * Highlights the specified lines the default colour
         *
         * @param fileId the id of the file
         * @param lines the lines to highlight
         */
        function highlight(fileId, lines, colour) {
            var first = true;
            for (var i = 0; i < lines.length; i++) { //loop through all the lines in the file
                var lineNum = lines[i]; //fetch the match linked to this line

                if (first) {
                    var line = $("pre[data-file-id='"+fileId+"']").find("[data-range='"+lineNum+"']");
                    var position = parseInt(line.css("top"), 10);

                    //Scroll the file div to the top of the line
                    // if (position != null) {
                    $("#id-" + fileId).find(".line-numbers").scrollTop(
                        position
                    );
                    // }

                    //Ensure that the report match box is at the same position as the first line
                    var offset = line.offset();
                    if (offset != null) {
                        $("#report-match-info").css("margin-top", offset.top - $('[data-js="comparison"]').offset().top);
                    }

                    first = false;
                }

                //change the colour of each line
                $("pre[data-file-id='"+fileId+"']").find("[data-range='"+lineNum+"']").css(gradient(rgba(colour)));
            }
        }

        /**
         * Shows the details of a match: highlights all the lines and
         * displays the details of the match at the bottom of the screen
         *
         * @param matchId
         */
        function showMatch(matchId) {
            //Check if there is an active match
            var row;
            if (active >= 0) {
                //Find the row in the table
                row = $("#row-" + active);
                //Remove the active class and display the show button
                row.removeClass("active");
                row.find("[data-js='match-show']").removeClass("d-none");
                row.find("[data-js='match-hide']").addClass("d-none");
            }

            active = matchId; //set the active match id
            var match = matches[active]; //fetch the details of the match

            //Double check that the match exists
            if (match == null) {
                hideAll();
                return;
            }

            //Update the details on the match info area
            var area = $("#match-info");
            var compareArea = true;

            //Check if on the report page
            if (!area.length) {
                area = $("#report-match-info");
                compareArea = false;
            }

            //Update the text
            area.find("#match-reason").text(match.reason);
            area.find("#match-score").text(match.score);
            area.find(".match-colour").css("background-color", match.colour);

            //Show the file code on the report page
            if (!compareArea) {
                //Load the details of the first match
                var submission = match.file1Submission; //the submission id of the match
                var lines = match.file1Lines.join(); //the lines to highlight
                var file =  match.file1Id; //the id of the file
                var name = match.file1Name; //the name of the file

                //Check if we're loading the wrong match
                if (submissionId == submission) {
                    //Therefore, load the details of the second match
                    submission = match.file2Submission;
                    lines = match.file2Lines.join();
                    file =  match.file2Id;
                    name = match.file2Name;
                }

                //Refresh the code area
                area.find("#match-code").html('<pre class="line-numbers" style="height: 500px; resize: vertical"\n' +
                    'data-line="'+lines+'"\n' +
                    'data-src="/dashboard/workspaces/manage/'+workspaceId+'/submission/'+submission+'/file/'+file+'/'+name+'"></pre>');
                Prism.fileHighlight();

                loadingReport = true;
            }

            area.show(); //show the info area

            //Find the row in the table
            row = $("#row-" + active);
            //Add the active class and display the hide button
            row.addClass("active");
            row.find("[data-js='match-show']").addClass("d-none");
            row.find("[data-js='match-hide']").removeClass("d-none");

            //Collapse all files except for the two involved
            $("[data-js='comparison']").find(".collapse:not([id=id-"+match.file1Id+"],[id=id-"+match.file2Id+"])").collapse('hide');
            $("#id-"+match.file1Id).collapse('show');
            $("#id-"+match.file2Id).collapse('show');

            //Remove all the line highlights
            $(".line-highlight").each(function() {
                $(this).css('background', "");
            });

            //Highlight the lines involved with this match
            highlight(match.file1Id, match.file1Lines, match.colour);
            highlight(match.file2Id, match.file2Lines, match.colour);

            var height;
            //Calculate the height to scroll the window to
            if (compareArea) {
                //Top of the collapse area if on the compare page
                height = $("[data-js='comparison']").offset().top;
                if ($("#matches-container").hasClass("sticky-top")) {
                    height -= $("#matches-container").height();
                }
            } else {
                //The top of the first highlighted line
                height = $("#report-match-info").offset().top - 10;
                if ($("#matches-container").hasClass("sticky-top")) {
                    height -= $("#matches-container").height();
                }
            }

            //Scroll the window
            $("html, body").scrollTop(
                height
            );
        }

        /**
         * Sets the colour of each highlighted line to the colour for the
         * relevant line and hides the details of the match at the bottom
         * of the screen.
         */
        function hideAll() {
            //Check if there is an active match
            if (active >= 0) {
                //Find the row in the table
                var row = $("#row-" + active);
                //Remove the active class and display the show button
                row.removeClass("active");
                row.find("[data-js='match-show']").removeClass("d-none");
                row.find("[data-js='match-hide']").addClass("d-none");
            }

            //Hide the match info area
            $("#match-info").slideUp();
            $("#report-match-info").slideUp();

            //Set the colour of each highlighted line
            $(".line-highlight").each(function() {
                var input = $(this);
                var lineNum = input.attr("data-range"); //fetch the line element
                var fileId = input.closest("pre").attr("data-file-id"); //get the file id of the line
                if (fileId == null) {
                    return;
                }

                var matchId = lineMap[fileId]['visible'][lineNum]; //get the match of the line

                if (matchId != null) {
                    var match = matches[matchId]; //find the match
                    if (match != null) {
                        input.css(gradient(rgba(match.colour))); //set the colour of the line
                    }
                }
            });

            active = -1;
        }

        /**
         * Binds all the click events for the compare/report pages
         */
        function bind() {
            //Hide the info area(s)
            $("#match-info").hide();
            $("#report-match-info").hide();

            //Listener for click events on the "previous" button in the match details div
            $("[data-js='match-previous']").unbind();
            $("[data-js='match-previous']").click(function(e) {
                var previous = parseInt(active, 10) - 1;

                if (previous < 0) {
                    previous = Object.keys(matches).length-1;
                }

                showMatch(previous);
            });

            //Listener for click events on the "next" button in the match details div
            $("[data-js='match-next']").unbind();
            $("[data-js='match-next']").click(function(e) {
                var next = parseInt(active, 10) + 1;

                if (next >= Object.keys(matches).length) {
                    next = 0;
                }

                showMatch(next);
            });

            //Listener for click events on the "show" button next to each match
            $("[data-js='match-show']").unbind();
            $("[data-js='match-show']").click(function(e) {
                var input = $(this);
                showMatch(input.attr("data-js-target"));
            });

            //Listener for click events on the "hide" button next to each match
            $("[data-js='match-hide']").unbind();
            $("[data-js='match-hide']").click(function(e) {
                hideAll();
            });

            //List for click events on the "toggle table" button
            $("[data-js='matches-list']").unbind();
            $("[data-js='matches-list']").click(function(e) {
                $("[data-js='matches-list']").each(function(e) {
                   $(this).toggleClass("d-none");
                });
                $("#matches-table-container").toggleClass("d-none");
            });

            //Hides the match when a file is collapsed
            $('.accordion').on('hide.bs.collapse', function () {
                hideAll();
            })
        }

        //Runs when the file contents finish loading in
        Prism.hooks.add('complete', function() {
            if (loadingReport) {
                var area = $("#report-match-info");

                if (area.length) {
                    var first = true;

                    area.find(".line-highlight").each(function(e){
                        var input = $(this);
                        input.css(gradient(rgba("#f47b2a")));

                        if (first) {
                            //Scroll the file div to the top of the line
                            var position = input.position();
                            if (position != null) {
                                area.find(".line-numbers").scrollTop(
                                    position.top
                                );
                            }

                            first = false;
                        }
                    });
                }
            } else {
                //Listens for click events anywhere in the code area, only if on the compare page
                if ($("#match-info").length) {
                    $(".code-toolbar").unbind();
                    $(".code-toolbar").click(function(e) {
                        hideAll();
                    });
                }

                //Listens for click events on highlighted lines
                $(".line-highlight").unbind();
                $(".line-highlight").click(function(e) {
                    //Only run if there is no active match
                    if (active < 0) {
                        var input = $(this);

                        var lineNum = input.attr("data-range"); //fetch the line element
                        var fileId = input.closest("pre").attr("data-file-id"); //get the file id of the line
                        var matchId = lineMap[fileId]['visible'][lineNum];  //get the match of the line

                        if (matchId != null) {
                            showMatch(matchId);
                        }

                        //Stops the [data-js='comparison'] event being called which
                        //would undo the effects of this event by hiding the match
                        //details
                        e.stopPropagation();
                    }
                });

                hideAll();
            }
        });

        bind();
    }
}

/**
 *
 * @param input
 */
function loadAreaAjax(input){
    getAjax(
        input.attr("data-js-href"),
        function(result, status, xhr) {
            input.html(result);
        }
    );
}

/**
 *
 */
function loadArea() {
    $("[data-js='area']").each(function () {
        var input = $(this);
        input.attr("data-js", "area-loaded");
        loadAreaAjax(input);
    });
}

/**
 *
 */
function loadAreaTrigger() {
    $("[data-js='triggerArea']").each(function () {
        var input = $(this);
        var target = input.attr("data-js-target");

        input.remove();
        $(target).html(loadingHTML);
        loadAreaAjax($(target));
    });
}

/**
 *
 */
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

/**
 *
 */
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

/**
 *
 */
function radioChange(){
    $("[data-js='radio-div']").unbind();
    $("[data-js='radio-div']").each(function() {
        var input = $(this);
        var name = input.attr("name");

        $("."+name).hide();
        $("."+name).removeClass("d-none");
    });
    $("[data-js='radio-div']").on('change', function () {
        var input = $(this);
        var value = input.val();
        var name = input.attr("name");

        $("."+name+":not(#"+value+")").slideUp();
        $("#"+value).slideDown();

        return false;
    });
}

/**
 *
 */
function usernameChange(){
    $("[data-js='triggerNameChange']").each(function () {
        var input = $(this);

        input.remove();
        var name = $('#username').val();
        $('#account-username').text(name);
    });
}

/**
 *
 */
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

/**
 *
 */
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
        json.nodes.sort(sortByLabel);

        var dataURL = "";

        function sortByLabel(a, b){
            var aName = a.label.toLowerCase();
            var bName = b.label.toLowerCase();
            return ((aName < bName) ? -1 : ((aName > bName) ? 1 : 0));
        }

        function addNode(id) {
            var result = $.grep(json.nodes, function( n, i ) {
                return n.id == id;
            });

            if (result.length == 1) {
                var node = result[0];

                try {
                    nodes.add({
                        id: node.id,
                        label: node.label,
                        color: colours[node.group]
                    });
                }
                catch (err) { }
            }
        }

        function addEdge(id1, id2) {
            var edge1 = $.grep(json.matches, function( n, i ) {
                return n.to == id1 && n.from == id2;
            });

            var edge2 = $.grep(json.matches, function( n, i ) {
                return n.to == id2 && n.from == id1;
            });

            var array = $.merge(edge1, edge2);

            var edge = array[0];

            if (array.length > 1 && edge.score < array[1].score) {
                var node = array[1];
            }

            var id = Math.min(edge.to, edge.from) + "_" + Math.max(edge.to, edge.from);

            try {
                edges.add({
                    id: id,
                    from: edge.from,
                    to: edge.to,
                    color: colours[edge.group]
                });
            }
            catch (err) { }
        }

        function addMatches(id) {
            var to = $.grep(json.matches, function( n, i ) {
                return n.to == id;
            });

            var from = $.grep(json.matches, function( n, i ) {
                return n.from == id;
            });

            var array = $.merge(to, from);

            for (var i = 0; i < array.length; i++) {
                var match = array[i];
                addEdge(match.to, match.from);
            }
        }

        function addMatchesIncNodes(id) {
            var to = $.grep(json.matches, function( n, i ) {
                return n.to == id;
            });

            for (var i = 0; i < to.length; i++) {
                var match = to[i];
                addEdge(match.to, match.from);
            }

            var from = $.grep(json.matches, function( n, i ) {
                return n.from == id;
            });

            for (var i = 0; i < from.length; i++) {
                var match = from[i];
                addNode(match.to);
                addEdge(match.to, match.from);
            }

            update();
        }

        function clickEvent() {
            var edgeDelete = $("[data-js='edgeDelete']");
            var nodeDelete = $("[data-js='nodeDelete']");
            var nodeMatches = $("[data-js='nodeMatches']");

            if (network.getSelectedEdges().length == 0) {
                edgeDelete.prop("disabled", true);
                edgeDelete.addClass("disabled");
            } else {
                edgeDelete.prop("disabled", false);
                edgeDelete.removeClass("disabled");
            }

            if (network.getSelectedNodes().length == 0) {
                nodeDelete.prop("disabled", true);
                nodeDelete.addClass("disabled");

                nodeMatches.prop("disabled", true);
                nodeMatches.addClass("disabled");
            } else {
                nodeDelete.prop("disabled", false);
                nodeDelete.removeClass("disabled");

                nodeMatches.prop("disabled", false);
                nodeMatches.removeClass("disabled");
            }
        }


        function bindEvents() {
            $("[data-js='submissionAdd']").unbind();
            $("[data-js='submissionAdd']").click(function () {
                var input = $(this);
                var target = input.attr("data-js-target");

                addNode(target);
                addMatches(target);
                update();
            });

            $("[data-js='submissionMatches']").unbind();
            $("[data-js='submissionMatches']").click(function () {
                var input = $(this);
                var target = input.attr("data-js-target");

                addMatchesIncNodes(target);
            });

            $("[data-js='submissionDelete']").unbind();
            $("[data-js='submissionDelete']").click(function () {
                var input = $(this);
                var target = input.attr("data-js-target");

                nodes.remove({id: target});
                update();
            });
        }

        function update() {
            includedArea.html("");
            excludedArea.html("");

            var visible = 0;
            var invisible = 0;

            for(var i = 0; i < json.nodes.length; i++) {
                var submission = json.nodes[i];
                var node = nodes.get(submission.id);

                if (node == null) { //Not visible on graph
                    var copy = excludedTemplate.clone();
                    copy.find(".js-label").text(submission.label);
                    copy.find(".js-id").attr("data-js-target", submission.id);
                    excludedArea.append(copy.html());

                    invisible++;
                } else { //Visible in graph
                    var copy = includedTemplate.clone();
                    copy.find(".js-label").text(submission.label);
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
                addMatchesIncNodes(selectedNodes[i]);
            }
        });

        $("[data-js='graphDownload']").click(function () {
            var input = $(this);
            input.blur();
            input.attr("href", dataURL);
        });

        update();

        var start = getParameter("start");
        if (start.length > 0) {
            addNode(start);
            addMatchesIncNodes(start);
        }
    }
}

/**
 *
 */
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

/**
 *
 */
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

/**
 *
 * @param url
 * @param success
 */
function getAjax(url, success) {
    var data = {
        ajax: "true"
    };
    submitAjax(url, data, success, "GET");
}

/**
 *
 * @param url
 * @param data
 * @param success
 * @param type
 */
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

/**
 *
 */
function displayTooltips() {
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover()
}

/**
 * Fetches a GET parameter:
 * FROM: http://www.jquerybyexample.net/2012/06/get-url-parameters-using-jquery.html
 *
 * @param name the name of the parameter
 *
 * @returns {string} the value of the parameter
 */
function getParameter(name) {
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == name) {
            return sParameterName[1];
        }
    }

    return "";
}
/**
 *
 */
function rebindEvents() {
    loadArea();
    displayTooltips();
    loadAreaInputTrigger();
    modalLink();
    submitForm();
    displayModalLinks();
    loadAreaTrigger();
    loadAreaLink();
    usernameChange();
    loadNetworkGraph();
    radioChange();
}

/**
 *
 */
$(function () {
    rebindEvents();
    $('form[data-js="autoSubmit"]').submit();
    submissionResultsPage();
    // submissionReportPage();
});