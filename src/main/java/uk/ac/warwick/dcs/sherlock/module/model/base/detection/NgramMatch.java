package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.io.Serializable;

// contains the match info for each ngram match case, does not contain the file info as that is passed separately.
public class NgramMatch implements Serializable {
    public Tuple<Integer, Integer> reference_lines;
    public Tuple<Integer, Integer> check_lines;
    public float similarity;

    NgramMatch(int refStart, int refEnd, int checkStart, int checkEnd, float similarity) {
        reference_lines = new Tuple<>(refStart, refEnd);
        check_lines = new Tuple<>(checkStart, checkEnd);
        this.similarity = similarity;
    }

}

// TODO comment
