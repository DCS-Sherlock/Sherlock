/*
    SHERLOCK Web UI
 */

/**
 * The HTML content to replace a div with when reloading the content
 * @type {string}
 */
var loadingHTML = '<img src="/img/load.gif" class="mx-auto d-block" height="75px">';

var loadedResults = false;

/**
 * Performs multiple functions on the compare and report pages:
 * - Changes the colour of the highlighted lines according to the match colour
 * - If you click on a highlighted line, it'll display the details for that match
 * - If you click "view" on a match, it'll display the details for that match
 * - Toggles the visibility of the matches table
 *
 * Please note that the following changes must be made if Prism is updated!:
 * "pointer-events: none" must be removed from the .line-highlight
 * class in prism.css
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

        var active = -1; // which match is active
        var loaded = 0; // how many files have loaded
        var failed = 0; // how many files have failed to load
        var showing = false; // true if showMatch is running
        var printed = false; // true if the print dialog has been shown
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
                    $("#id-" + fileId).find(".line-numbers").scrollTop(
                        position
                    );

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
            showing = true;

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
            area.find("#match-reason").text("#" + matchId + ": " + match.reason);
            area.find("#match-score").text(match.score);
            area.find(".match-colour").css("background-color", match.colour);

            //Show the file code on the report page
            if (!compareArea) {
                loadingReport = true;
                area.find("#match-code").html("");
                for(var i = 0; i < match.matches.length; i++) {
                    var obj = match.matches[i];

                    if (obj.submission != submissionId) {
                        //Refresh the code area
                        area.find("#match-code").append('<div class="card-header">'+obj.submissionName+': '+obj.displayName+'</div><pre class="line-numbers mt-0" style="height: 300px; resize: vertical"\n' +
                            'data-line="'+obj.lines+'"\n' +
                            'data-src="/dashboard/workspaces/manage/'+workspaceId+'/submission/'+obj.submission+'/file/'+obj.id+'/'+obj.name+'"></pre>');
                    }
                }
                Prism.fileHighlight();
            }

            area.show(); //show the info area

            //Find the row in the table
            row = $("#row-" + active);

            //Add the active class and display the hide button
            row.addClass("active");
            row.find("[data-js='match-show']").addClass("d-none");
            row.find("[data-js='match-hide']").removeClass("d-none");


            //Remove all the line highlights
            $(".line-highlight").each(function() {
                $(this).css('background', "");
            });

            //Collapse all the files
            $(".collapse").removeClass('show');

            //Highlight the lines involved with this match
            for(var i = 0; i < match.matches.length; i++) {
                var obj = match.matches[i];
                //Show the files involved
                $("#id-"+obj.id).addClass('show');
                //Highlight the lines
                highlight(obj.id, obj.lines, match.colour)
            }

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

            showing = false;
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

            $("[data-js='matches-left']").unbind();
            $("[data-js='matches-left']").click(function(e) {
                $("#left").addClass("col-lg-9");
                $("#left").removeClass("col-lg-6");
                $("#left").removeClass("col-lg-3");

                $("#right").addClass("col-lg-3");
                $("#right").removeClass("col-lg-6");
                $("#right").removeClass("col-lg-9");

                $("[data-js='matches-left']").addClass("d-none");
                $("[data-js='matches-right']").removeClass("d-none");
                $(".restore-left").removeClass("d-none");
                $(".restore-right").addClass("d-none");
            });

            $("[data-js='matches-right']").unbind();
            $("[data-js='matches-right']").click(function(e) {
                $("#right").addClass("col-lg-9");
                $("#right").removeClass("col-lg-6");
                $("#right").removeClass("col-lg-3");

                $("#left").addClass("col-lg-3");
                $("#left").removeClass("col-lg-6");
                $("#left").removeClass("col-lg-9");

                $("[data-js='matches-left']").removeClass("d-none");
                $("[data-js='matches-right']").addClass("d-none");
                $(".restore-left").addClass("d-none");
                $(".restore-right").removeClass("d-none");
            });

            $("[data-js='matches-restore']").unbind();
            $("[data-js='matches-restore']").click(function(e) {
                $("#left").addClass("col-lg-6");
                $("#left").removeClass("col-lg-9");
                $("#left").removeClass("col-lg-3");

                $("#right").addClass("col-lg-6");
                $("#right").removeClass("col-lg-3");
                $("#right").removeClass("col-lg-9");

                $("[data-js='matches-left']").removeClass("d-none");
                $("[data-js='matches-right']").removeClass("d-none");
                $(".restore-left").addClass("d-none");
                $(".restore-right").addClass("d-none");
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
                if (showing == false) {
                    hideAll();
                }
            });

            //Hides the match when a file is opened
            $('.accordion').on('show.bs.collapse', function () {
                if (showing == false) {
                    hideAll();
                }
            });
        }

        function printEvent() {
            if (isPrinting()) {
                //Count the number of files that failed to load
                failed = 0;
                $("pre").each(function() {
                    var input = $(this);
                    var content = input.html();

                    if (content.includes("✖ Error")) {
                        failed++;
                    }
                });

                //Update the progress bar
                var progress = (loaded/$("pre").length) * 100;
                $("#loaded-progress").css("width", progress+"%");
                var progress2 = (failed/$("pre").length) * 100;
                $("#failed-progress").css("width", progress2+"%");

                if ((loaded+failed) >= $("pre").length && printed == false) {
                    printed = true;

                    setTimeout(function() {
                        //Show the print button
                        $("#print").removeClass("d-none");

                        //Warn the user if some files failed
                        if (failed != 0) {
                            alert("Some of the files may have failed to load. Please print the report to PDF, or use the print preview, to check that all the files have loaded successfully.");
                        }

                        window.print();
                    }, 1000);
                }
            }
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

                hideAll();
                loaded++;
                printEvent();
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
        });

        bind();
    }
}

/**
 * Load an area of a page using the href attribute as the source
 *
 * @param input the area to load
 */
function loadArea(input){
    submitGetAjax(
        input.attr("data-js-href"),
        function(result, status, xhr) {
            input.html(result);
        },
        input
    );
}

/**
 * For each area, load the requested sub-page and replace the area contents
 */
function triggerArea() {
    $("[data-js='area']").each(function () {
        var input = $(this);
        input.attr("data-js", "area-loaded");
        loadArea(input);
    });
}

/**
 * For each trigger element, trigger the load area fn
 */
function triggerLoadArea() {
    $("[data-js='triggerArea']").each(function () {
        var input = $(this);
        var target = input.attr("data-js-target");

        input.remove();
        $(target).html(loadingHTML);
        loadArea($(target));
    });
}

/**
 * Bind events for the trigger area links
 */
function bindAreaLink() {
    $("[data-js='triggerAreaLink']").unbind();
    $("[data-js='triggerAreaLink']").click(function () {
        var input = $(this);
        var target = input.attr("data-js-target");

        $(target).html(loadingHTML);
        loadArea($(target));
        $("#modal").modal('hide');
    });
}

/**
 * Bind change events for the select inputs
 */
function bindSelectChange(){
    $("select[data-js='select']").unbind();
    $("select[data-js='select']").on('change', function () {
        var input = $(this);
        var value =  input.val();
        var target = input.attr("data-js-target");
        var url = input.attr("data-js-href");

        $(target).html(loadingHTML);

        submitGetAjax(
            url + value,
            function(result, status, xhr) {
                $(target).html(result);
            },
            target
        );

        return false;
    });
}

/**
 * Bind change events for the radio inputs on the add submissions page
 */
function bindRadioChange() {
    $("[data-js='radio-div']").unbind();
    $("[data-js='radio-div']").on('change', function () {
        var input = $(this);
        var value = input.val();
        var name = input.attr("name");

        $("."+name+":not(#"+value+")").slideUp();
        $("#"+value).slideDown();

        Cookies.set("radio_" + name, value);

        return false;
    });
    $("[data-js='radio-div']").each(function() {
        var input = $(this);
        var name = input.attr("name");
        var value = Cookies.get("radio_" + name);
        console.log(name, value);

        var checked = false;
        if (value != null) {
            checked = true;
        }

        $('input:radio[name="'+name+'"][value="'+value+'"]').attr('checked', checked).trigger('change');

        $("."+name).removeClass("d-none");
        $("."+name).hide();
    });
}

/**
 * If the username was changed on the account page, update the name on the navigation bar
 */
function bindUsernameChange(){
    $("[data-js='triggerNameChange']").each(function () {
        var input = $(this);

        input.remove();
        var name = $('#username').val();
        $('#account-username').text(name);
    });
}

/**
 * Bind click events for modal links
 */
function bindModalLinks(){
    $("[data-js='modal']").unbind();
    $("[data-js='modal']").click(function () {
        var input = $(this);
        var url = input.attr("href");

        input.prop("disabled", true);
        input.addClass("disabled");

        submitGetAjax(
            url,
            function(result, status, xhr) {
                $("#modal").html(result);
                $("#modal").modal('show');
                bindSelectChange();
            },
            $("#modal")
        );

        input.prop("disabled", false);
        input.removeClass("disabled");

        return false;
    });
}

/**
 * Loads the graph and manages the tables on the network graph page
 */
function networkGraphPage() {
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
 * Bind form submit events
 */
function bindForms(){
    $("[data-js='form']").unbind();
    $("[data-js='form']").submit(function () {
        var input = $(this);
        var url = input.attr("action");
        var target = $(input.attr("data-js-target"));

        input.find("button[type=submit]").prop("disabled", true);
        input.find("button[type=submit]").addClass("disabled");

        var data = new FormData(this);
        data.append("ajax", "true");

        submitGenericAjax(
            url,
            data,
            function(result, status, xhr) {
                target.html(result);
            },
            "POST",
            target
        );

        return false;
    });
}

/**
 * On modal pages, hide the close link and show the modal close button
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
 * Enables bootstrap tooltips/popovers
 */
function bindTooltips() {
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover()
}

/**
 * Enables DataTables support
 */
function bindTables() {
    $.fn.dataTable.ext.errMode = 'throw';

    $('[data-js="table"]').dataTable( {
        "paging": true,
        "pagingType": "simple_numbers",
        "bLengthChange": false,
        "bInfo": false,
        "searching": true,
        "autoWidth": false,
        "language": {
            search: '<div class="input-group"><div class="input-group-prepend"><span class="input-group-text oi oi-magnifying-glass"></span></div>'
        }
    } );

    $("[data-js='table']").each(function () {
        var input = $(this);
        input.attr("data-js", "table-loaded");
    });

    $('[data-js="table-matches"]').dataTable( {
        "paging": false,
        "bInfo": false,
        "searching": true,
        "autoWidth": false,
        "language": {
            search: '<div class="input-group"><div class="input-group-prepend"><span class="input-group-text oi oi-magnifying-glass"></span></div>'
        },
        "columnDefs": [
            { "orderData": [ 2 ],    "targets": 3 },
            { orderable: false, targets: [4] }
        ]
    } );

    $("[data-js='table-matches']").each(function () {
        var input = $(this);
        input.attr("data-js", "table-loaded");
    });

    $(".dataTables_filter").each(function() {
       var parent = $(this).parent();
        parent.removeClass("col-md-6");
        parent.addClass("col-md-12");
    });
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
 * Runs a GET ajax request
 *
 * @param url the url to get/post to
 * @param success the success callback
 * @param target
 */
function submitGetAjax(url, success, target) {
    var data = {
        ajax: "true"
    };
    submitGenericAjax(url, data, success, "GET", target);
}

/**
 * Runs an ajax request
 *
 * @param url the url to get/post to
 * @param data the data to include with the request
 * @param success the success callback
 * @param type post type: GET/POST
 * @param target the target to update if there was an error
 */
function submitGenericAjax(url, data, success, type, target) {
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
            var copy = $('#javascript-error-clone').clone();

            if (copy.find("."+xhr.status).length == 1){
                copy.find(".alert").html(copy.find("."+xhr.status).html());
            } else {
                copy.find(".alert").html(copy.find(".other").html());
                copy.find('.status-code').text(xhr.status);
            }

            if (target.attr("id") == "modal" || $("#modal").find(target).length == 1) {
                $("#modal").modal('hide');
                $("#javascript-error").html(copy.html());
            } else {
                target.html(copy.html());
            }
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
 * Rebind the jQuery events when the page has been updated
 */
function rebindEvents() {
    displayModalLinks();
    triggerArea();
    triggerLoadArea();
    bindTooltips();
    bindSelectChange();
    bindModalLinks();
    bindForms();
    bindAreaLink();
    bindUsernameChange();
    bindRadioChange();
    bindTables();
}

/**
 * Runs when the page has loaded
 */
$(function () {
    $('form[data-js="autoSubmit"]').submit(); //automatically submits the login page when running locally

    rebindEvents();

    submissionResultsPage();
    $(window).focus(function() {
        if (loadedResults == false) {
            $("[data-js='match-hide']").trigger("click");
            loadedResults = true;
        }
    });

    networkGraphPage();

    // FROM: https://itsolutionstuff.com/post/how-to-remove-query-string-from-urlexample.html
    var uri = window.location.toString();
    var clean_uri = uri;
    if (uri.indexOf("?") > 0) {
        clean_uri = uri.substring(0, uri.indexOf("?"));
        window.history.replaceState({}, document.title, clean_uri);
    }

    // Check the status of a job on the results page every 10 seconds
    if ($("#job-status").length && $("#job-progress").length) {
        var json_uri = clean_uri + "/json";

        setInterval(function() {
            $.getJSON(json_uri, function(data) {
                // Update the status badge
                $("#job-status").html(data.message);

                // Update the progress bar
                var percent = (data.progress) + "%";
                $("#job-progress").css("width", percent);
                $("#job-progress span").html(percent);

                // If finished, reload the page to view the results
                if (data.message == "Finished") {
                    location.reload();
                }
            });
        }, 5000);
    }

    if ($("#queue-parent").length) {
        setInterval(function() {
            submitGetAjax("/dashboard/index/queue", function(result, status, xhr) {
                $("#queue-parent").html(result);
            }, $("#modal"))
        }, 5000);
    }
});

window.addEventListener("load", function(){
    window.cookieconsent.initialise({
        "palette": {
            "popup": {
                "background": "#4e5d6c",
                "text": "#ffffff"
            },
            "button": {
                "background": "#df691a",
                "text": "#ffffff"
            }
        },
        "position": "top",
        "static": true,
        "content": {
            "href": "/privacy"
        }
    })});