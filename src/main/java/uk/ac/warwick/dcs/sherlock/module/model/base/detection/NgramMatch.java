package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;

import java.io.Serializable;
import java.util.ArrayList;

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
    /**
     * The line positions of both blocks.
     */
    public ArrayList<Tuple<Integer, Integer>> lines;    // array list used for type safety of generics
    /**
     * The similarity between the section of both files.
     */
    public float similarity;
    /**
     * The two files with matching sections.
     */
    public ISourceFile[] files;
    /**
     * Used to allow skipping during score methods if the math is considered common. Is now redundant due to
     * depreciation, but has been left in if needed in future use cases.
     */
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
        // init the array list
        lines = new ArrayList<>();
        // fill with line positions
        lines.add(new Tuple<>(refStart, refEnd));
        lines.add(new Tuple<>(checkStart, checkEnd));

        this.similarity = similarity;
        // init the array to a pair
        files = new ISourceFile[2];
        // store the file objects (pointers, the same pointers should be used globally)
        files[0] = file1;
        files[1] = file2;
        // default common as false
        this.common = false;
    }

    /**
     * Checks if the two stored blocks are the same.
     * @param pair The match to check for equality.
     * @return True if the blocks are the same.
     */
    public boolean equals(NgramMatch pair) {
        return this.lines.get(0).equals(pair.lines.get(0)) && this.lines.get(1).equals(pair.lines.get(1));
    }
}