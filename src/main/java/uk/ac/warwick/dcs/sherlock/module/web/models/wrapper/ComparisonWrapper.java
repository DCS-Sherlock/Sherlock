package uk.ac.warwick.dcs.sherlock.module.web.models.wrapper;

import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.engine.component.ISubmission;
import uk.ac.warwick.dcs.sherlock.module.web.models.results.CodeBlock;
import uk.ac.warwick.dcs.sherlock.module.web.models.results.FileMapper;
import uk.ac.warwick.dcs.sherlock.module.web.models.results.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComparisonWrapper {
    private ISubmission submission1;
    private ISubmission submission2;

    private List<Match> matches;

    private FileMapper fileMapper;

    public ComparisonWrapper(ISubmission submission1, ISubmission submission2) {
        this.submission1 = submission1;
        this.submission2 = submission2;

        this.matches = new ArrayList<>();

        //TODO: fetch real data from report generator

        //Generate fake data
        {
            long file1Id = submission1.getAllFiles().get(0).getPersistentId();
            long file2Id = submission2.getAllFiles().get(0).getPersistentId();

            for (int i = 1; i < 5; i++) {
                List<CodeBlock> list1 = new ArrayList<>();
                list1.add(new CodeBlock(this.tempRandomNumberInRange(0, 20), this.tempRandomNumberInRange(21, 40)));
                List<CodeBlock> list2 = new ArrayList<>();
                list2.add(new CodeBlock(this.tempRandomNumberInRange(0, 20), this.tempRandomNumberInRange(21, 40)));

                matches.add(new Match(file1Id, list1, file2Id, list2, "Match "+ i +" Reason", this.tempRandomNumberInRange(0, 100)));
                if (i == 1) {
                    matches.add(new Match(file1Id, list1, file2Id, list2, "Match "+ i +" COPY Reason", this.tempRandomNumberInRange(0, 100)));
                }
            }
        }

        //Loop through the matches, setting the ids
        for (int i = 0; i < matches.size(); i++) {
            Match match = matches.get(i);
            match.setId(i);
        }

        this.fileMapper = new FileMapper(this.matches);
    }

    public ISubmission getSubmission1() {
        return submission1;
    }

    public ISubmission getSubmission2() {
        return submission2;
    }

    public String getLineToMatchIdJSON(){
        return fileMapper.toJSON().toString();
    }

    public String getMatchesJSON() {
        JSONObject object = new JSONObject();

        for (Match match : matches) {
            object.put(""+match.getId(), match.toJSON());
        }

        return object.toString();
    }

    public String getHighlightedLines(long fileId) {
        return fileMapper.getHighlightedLines(fileId);
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
