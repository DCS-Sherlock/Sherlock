package uk.ac.warwick.dcs.sherlock.module.web.helpers;

import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.SubmissionNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;

import java.awt.*;
import java.util.Random;

/**
 * Functions used by multiple classes involved with displaying the
 * analysis results to the user
 */
public class ResultsHelper {
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
     * Generates a random HEX colour code (e.g. "ffffff")
     *
     * @return the colour code without the "#" before
     */
    public static String randomColour() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        return Integer.toHexString(new Color(r, g, b).getRGB()).substring(2);
    }
}
