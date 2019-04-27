package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.CodeBlock;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.FileMatch;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.MapperException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Maps every line of a file that the system thinks is plagiarised to
 * one or more "match ids" (stored in the code block object)
 */
public class LineMapper {
    /**
     * The id of the mapped file
     */
    private long fileId;

    /**
     * The highest line number found in a matched code block
     */
    private int maxLineNum;

    /**
     * A temporary map used internally to calculate the other two maps
     */
    private SortedMap<Integer, List<CodeBlock>> tempMap;

    /**
     * Links every line number to a match id so that the UI can
     * highlight each line with the match's colour code
     *
     * NB: lines with no matches are excluded from the map
     */
    private SortedMap<Integer, Integer> visibleMatches;

    /**
     * Links every line number to a list of match ids that occur on that
     * line
     *
     * NB: lines with no matches are excluded from the map
     */
    private SortedMap<Integer, List<Integer>> allMatches;

    /**
     * Initialise an empty instance of line mapper
     *
     * @param fileId the id of the file being mapped
     */
    public LineMapper(long fileId) {
        this.visibleMatches = new TreeMap<>();
        this.allMatches = new TreeMap<>();
        this.tempMap = new TreeMap<>();
        this.fileId = fileId;
        this.maxLineNum = 0;
    }

    /**
     * Initialise the line mapper and fill with a list of matches
     *
     * @param fileId the id of the file being mapped
     * @param matches the list of matches to fill the map with
     */
    public LineMapper(long fileId, List<FileMatch> matches) {
        this.visibleMatches = new TreeMap<>();
        this.allMatches = new TreeMap<>();
        this.tempMap = new TreeMap<>();
        this.fileId = fileId;
        this.maxLineNum = 0;

        try {
            for (FileMatch match : matches) {
                this.AddMatch(match);
            }
        } catch (MapperException e) {
            //Should never occur in this situation
        }

        this.Fill();
    }

    /**
     * Fills the temp map with line numbers where matched code blocks start. If
     * multiple matches start on the same line, the list should have multiple
     * elements in it, all others should only contain one element.
     *
     * @param match the match to insert into the map
     *
     * @throws MapperException if add match was called after fill
     */
    public void AddMatch(FileMatch match) throws MapperException {
        //This function can only run if the fill function has not been ran
        if (visibleMatches.keySet().size() > 0) {
            throw new MapperException("Attempted to add match after map has already been filled.");
        }

        List<CodeBlock> blocks = new ArrayList<>();

        //Get the code blocks for this file
        for (Map.Entry<ISourceFile, List<CodeBlock>> entry : match.getMap().entrySet()) {
            ISourceFile entryFile = entry.getKey();
            List<CodeBlock> entryList = entry.getValue();

            if (fileId == entryFile.getPersistentId()) {
                blocks = entryList;
            }
        }

        //Loop through the code blocks (the list is empty if the match isn't relevant to this file)
        for (CodeBlock block : blocks) {
            List<CodeBlock> list = new ArrayList<>();

            if (tempMap.containsKey(block.getStartLine())) {
                list = tempMap.get(block.getStartLine());
            }

            //Store the max line num, used for the "Fill" method
            maxLineNum = Math.max(maxLineNum, block.getEndLine());

            list.add(new CodeBlock(block.getStartLine(), block.getEndLine(), match.getId()));

            tempMap.put(block.getStartLine(), list);
        }
    }

    /**
     * This function needs to be called after all the matches have been added
     * using the "addMatch" method, it will then fill in the gaps from the start
     * line number to the end line number for each code block.
     *
     * e.g. if there was a match from lines 2-4, the previous method would add
     * an entry on line 2, while this method will add values on lines 3 and 4.
     *
     * If there are overlaps, it will pick the newest code block and if multiple
     * code blocks start on the same line, it will pick the shortest one.
     *
     * e.g. If Block #1 has lines 2-7 and Block #2 has lines 3-5:
     * 1: None
     * 2: #1
     * 3: #2
     * 4: #2
     * 5: #2
     * 6: #1
     * 7: #1
     * 8: None
     *
     * This is not a perfect algorithm, and some matches/code blocks will not
     * be displayed at all however there will be a list of matches on the page
     * for the user to see all of them and it ensures that as many different
     * matches are displayed as possible.
     *
     * The function also populates a "allMatches" map which has a list of every
     * match found on each line. This is not currently used by anything else.
     */
    public void Fill() {
        Stack<CodeBlock> visibleStack = new Stack<>();

        Optional<CodeBlock> visibleBlock = Optional.empty();
        List<CodeBlock> activeBlocks = new ArrayList<>();

        //Loop through all the lines in the file up to the end of the last matched block
        for (int line = 0; line <= maxLineNum; line++) {
            //Check if the active highlight block has expired
            if (visibleBlock.isPresent() && visibleBlock.get().getEndLine() < line) {
                visibleBlock = Optional.empty(); //expire the block
            }

            //Remove inactive blocks from the active blocks list
            List<CodeBlock> remove = new ArrayList<>();
            for (CodeBlock block : activeBlocks) {
                if (block.getEndLine() < line) {
                    remove.add(block);
                }
            }
            activeBlocks.removeAll(remove);

            //Check if the line exists
            if (tempMap.containsKey(line) ) {
                //Check if actively writing
                if (visibleBlock.isPresent()) {
                    //Store in the stack if so
                    visibleStack.push(visibleBlock.get());
                }

                //Fetch the code blocks on the line
                List<CodeBlock> list = tempMap.get(line);

                //Sort by the end line number
                Collections.sort(list, Comparator.comparing(CodeBlock::getEndLine));

                boolean first = true;

                //Loop through the blocks on the line
                for (CodeBlock block : list) {
                    if (first) {
                        visibleBlock = Optional.of(block);
                        first = false;
                    } else {
                        visibleStack.push(block);
                    }
                    activeBlocks.add(block);
                }
            }

            //If there is nothing actively writing, loop through until the stack is empty
            while(!visibleBlock.isPresent() && !visibleStack.empty()) {
                //Fetch the first block
                CodeBlock block = visibleStack.pop();

                //Check if it has not expired
                if (block.getEndLine() > line) {
                    visibleBlock = Optional.of(block);
                }
            }

            //If actively writing, add the active blocks to the map
            if (visibleBlock.isPresent()) {
                visibleMatches.put(line, visibleBlock.get().getMatchId());
            }

            if (activeBlocks.size() > 0) {
                List<Integer> list = new ArrayList<>();

                for (CodeBlock block : activeBlocks) {
                    list.add(block.getMatchId());
                }

                allMatches.put(line, list);
            }
        }
    }

    /**
     * Convert this object to a JSON object, used by the JavaScript in the UI
     *
     * @return the JSON equivalent of this object
     */
    public JSONObject toJSON() {
        JSONObject highlight = new JSONObject();
        for (Map.Entry<Integer, Integer> entry : visibleMatches.entrySet()) {
            highlight.put(""+entry.getKey(), entry.getValue());
        }

        JSONObject all = new JSONObject();
        for (Map.Entry<Integer, List<Integer>> entry : allMatches.entrySet()) {
            JSONArray array = new JSONArray();

            for (Integer id : entry.getValue()) {
                array.put(id);
            }

            all.put(""+entry.getKey(), array);
        }

        JSONObject result = new JSONObject();
        result.put("visible", highlight);
        result.put("all", all);

        return result;
    }

    /**
     * Get the list of plagiarised line numbers converted to a comma separated list
     *
     * e.g. if lines 2-10 are mapped to a match, this would return "2,3,4,5,6,7,8,9,10"
     *
     * @return the comma separated list
     */
    public String getHighlightedLines() {
        return visibleMatches.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
    }
}