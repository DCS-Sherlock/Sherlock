package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.models.results.CodeBlock;
import uk.ac.warwick.dcs.sherlock.module.web.models.results.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReportWrapper {
    private ISubmission submission;

    private List<Match> matches;

    public ReportWrapper(ISubmission submission) {
        this.submission = submission;

        this.matches = new ArrayList<>();

        //TODO: fetch real data from report generator

        //Generate fake data
        {
            long file1Id = submission.getAllFiles().get(0).getPersistentId();

            for (int i = 1; i < 5; i++) {
                List<CodeBlock> list1 = new ArrayList<>();
                list1.add(new CodeBlock(this.tempRandomNumberInRange(0, 20), this.tempRandomNumberInRange(21, 40)));
                List<CodeBlock> list2 = new ArrayList<>();
                list2.add(new CodeBlock(this.tempRandomNumberInRange(0, 20), this.tempRandomNumberInRange(21, 40)));

                matches.add(new Match(file1Id, list1, file1Id, list2, "Match "+ i +" Reason", this.tempRandomNumberInRange(0, 100)));
                if (i == 1) {
                    matches.add(new Match(file1Id, list1, file1Id, list2, "Match "+ i +" COPY Reason", this.tempRandomNumberInRange(0, 100)));
                }
            }
        }

        //Loop through the matches, setting the ids
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            match.setId(i);
        }

//        this.fileMapper = new FileMapper(this.matches);
    }

    public ISubmission getSubmission() {
        return submission;
    }

    @SuppressWarnings("Duplicates")
    private int tempRandomNumberInRange(int min, int max) {
        if (min >= max) {
            return max;
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
