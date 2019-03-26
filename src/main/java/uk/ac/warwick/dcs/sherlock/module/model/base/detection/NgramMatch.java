package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.io.Serializable;

/**
 * Contains all data for a single matched pair.
 * <p>
 *     Contains:
 *     Start and end lines for block in reference file,
 *     Start and end lines for block in checked file,
 *     The float value denoting similarity between the 2 blocks,
 *     A pointer to the reference file (File1),
 *     A pointer to the checked file (File2).
 * </p>
 */
public class NgramMatch implements Serializable {
    public Tuple<Integer, Integer> reference_lines;
    public Tuple<Integer, Integer> check_lines;
    public float similarity;
    // TODO change these out for an array pair to allow iteration
    public ISourceFile file1;
    public ISourceFile file2;
    // used to allow skipping during score methods if the math is considered common
    public boolean common;


    /**
     * Constructor, stores all inputted data in the container object.
     * @param refStart The start line of the block in File1.
     * @param refEnd The end line of the block in File1.
     * @param checkStart The start line of the block in File2.
     * @param checkEnd The end line of the block in File2.
     * @param similarity The similarity between the 2 blocks.
     * @param file1 The first file.
     * @param file2 The second file.
     */
    NgramMatch(int refStart, int refEnd, int checkStart, int checkEnd, float similarity, ISourceFile file1, ISourceFile file2) {
        reference_lines = new Tuple<>(refStart, refEnd);
        check_lines = new Tuple<>(checkStart, checkEnd);
        this.similarity = similarity;
        this.file1 = file1;
        this.file2 = file2;
        // default common as false
        this.common = false;
    }

    /**
     * Checks if the two stored blocks are the same.
     * @param pair The match to check for equality.
     * @return True if the blocks are the same.
     */
    public boolean equals(NgramMatch pair) {
        return this.reference_lines.equals(pair.reference_lines) && this.check_lines.equals(pair.check_lines);
    }
}