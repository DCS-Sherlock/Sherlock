package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import uk.ac.warwick.dcs.sherlock.api.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.SubmissionNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.WorkspaceWrapper;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Functions used by multiple classes involved with displaying the
 * analysis results to the user
 */
public class ResultsHelper {
    /**
     * Array of colours for the get colour method
     */
    public static List<String> colours = Arrays.asList(
        "#ff4c4c",
        "#ff9932",
        "#ffff66",
        "#a6ff4c",
        "#4cff4c",
        "#66ffd8",
        "#99e5ff",
        "#a64cff",
        "#ff66ff",
        "#ff99cc",
        "#ffcce5",
        "#ff0000",
        "#ff8000",
        "#ffff00",
        "#80ff00",
        "#00ff00",
        "#00ffbf",
        "#00bfff",
        "#0040ff",
        "#8000ff",
        "#ff00ff",
        "#ff0080",
        "#b20000",
        "#cc6600",
        "#cccc00",
        "#66cc00",
        "#00b200",
        "#00cc98",
        "#0098cc",
        "#005f7f",
        "#002cb2",
        "#5900b2",
        "#b200b2",
        "#b20059",
        "#7f0000",
        "#994c00",
        "#999900",
        "#4c9900",
        "#007f00",
        "#009972",
        "#0085b2",
        "#001966",
        "#26004c",
        "#660066",
        "#4c0026"
    );

    /**
     * Tries to find the submission in the workspace supplied.
     *
     * @param workspaceWrapper the wrapper class of the workspace to search
     * @param submissionid the id of the submission to find
     *
     * @return the ISubmission object of the submission
     *
     * @throws SubmissionNotFound if the submission was not found in the workspace supplied
     */
    public static ISubmission getSubmission(
            WorkspaceWrapper workspaceWrapper,
            long submissionid) throws SubmissionNotFound {
        ISubmission submission = null;

        for (ISubmission temp : workspaceWrapper.getSubmissions()) {
            if (temp.getId() == submissionid) {
                submission = temp;
            }
        }

        if (submission == null) {
            throw new SubmissionNotFound("Submission not found");
        }

        return submission;
    }

    /**
     * Fetches a colour code for a match from the colour list above,
     * or randomly generates one if the index is out of bounds
     *
     * @param id the id/index of the match
     *
     * @return the hex colour code
     */
    public static String getColour(int id) {
        if (id < colours.size()) {
            return colours.get(id);
        }

        return randomColour();
    }

    /**
     * Generates a random HEX colour code (e.g. "#ffffff")
     *
     * @return the colour code
     */
    public static String randomColour() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        return "#" + Integer.toHexString(new Color(r, g, b).getRGB()).substring(2);
    }

    /**
     * All scores are grouped into 10 groups:
     * 0-10, 10-20, 20-30, 30-40, 40-50, 50-60, 60-70, 70-80, 80-90 or 90-100
     * Get the group this score belongs to.
     *
     * @param score the score to find the group of
     *
     * @return the score group
     */
    public static int getScoreGroup(float score) {
        if (score > 90) {
            return 9;
        } else if (score > 80) {
            return 8;
        } else if (score > 70) {
            return 7;
        } else if (score > 60) {
            return 6;
        } else if (score > 50) {
            return 5;
        } else if (score > 40) {
            return 4;
        } else if (score > 30) {
            return 3;
        } else if (score > 20) {
            return 2;
        } else if (score > 10) {
            return 1;
        }

        return 0;
    }
}
