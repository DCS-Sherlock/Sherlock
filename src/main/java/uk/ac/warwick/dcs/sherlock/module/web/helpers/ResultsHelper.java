package uk.ac.warwick.dcs.sherlock.module.web.helpers;

import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.SubmissionNotFound;
import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;

import java.awt.*;
import java.util.Random;

public class ResultsHelper {
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

    public static String randomColour() {
        Random random = new Random();

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        return Integer.toHexString(new Color(r, g, b).getRGB()).substring(2);
    }
}
