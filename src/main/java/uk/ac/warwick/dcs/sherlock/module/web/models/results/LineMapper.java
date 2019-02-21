package uk.ac.warwick.dcs.sherlock.module.web.models.results;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class LineMapper {
    private long fileId;
    private int maxLineNum;

    private SortedMap<Integer, List<CodeBlock>> map;

    public LineMapper(long fileId) {
        this.map = new TreeMap<>();
        this.fileId = fileId;
        this.maxLineNum = 0;
    }

    public void AddStartLines(Match match) {
        List<CodeBlock> blocks = new ArrayList<>();

        //get the code blocks for this file
        if (fileId == match.getFile1Id()) {
            blocks = match.getFile1CodeBlocks();
        } else if (fileId == match.getFile2Id()) {
            blocks = match.getFile2CodeBlocks();
        }

        //loop through the code blocks
        for (CodeBlock block : blocks) {
            List<CodeBlock> list = new ArrayList<>();
            if (map.containsKey(block.getStartLine())) {
                list = map.get(block.getStartLine());
            }

            //store the max line num, used for the fill in blocks method
            maxLineNum = Math.max(maxLineNum, block.getEndLine());

            list.add(new CodeBlock(block.getStartLine(), block.getEndLine(), match.getId()));
            map.put(block.getStartLine(), list);
        }
    }

    public void FillInBlocks() {
        Optional<List<CodeBlock>> active = Optional.empty();
        Stack<CodeBlock> stack = new Stack<>();

        //Loop through all the lines in the file up to the end of the last matched block
        for (int line = 1; line <= maxLineNum; line++) {
            //Check if the active block has expired
            if (active.isPresent() && active.get().get(0).getEndLine() < line) {
                active = Optional.empty(); //expire the block
            }

            //Check if the line exists
            if (map.containsKey(line) ) {
                //Check if actively writing
                if (active.isPresent()) {
                    //Store in the stack if so
                    for (CodeBlock block : active.get()) {
                        stack.push(block);
                    }
                }

                //Fetch the code blocks on the line
                List<CodeBlock> list = map.get(line);

                //get the min end line value
                //TODO: remove unnecessary sorting
                Collections.sort(list, Comparator.comparing(CodeBlock::getEndLine));
                int min = list.get(0).getEndLine();

                List<CodeBlock> activeList = new ArrayList<>();

                for (CodeBlock block : list) {
                    if (block.getEndLine() == min) {
                        activeList.add(block);
                    } else {
                        stack.push(block);
                    }
                }

                active = Optional.of(activeList);
            }

            while(!active.isPresent() && !stack.empty()) {
                CodeBlock block = stack.pop();

                if (block.getEndLine() > line) {
                    List<CodeBlock> activeList = new ArrayList<>();
                    activeList.add(block);

                    while(!stack.empty() && block.getEndLine() == stack.peek().getEndLine()) {
                        activeList.add(stack.pop());
                    }

                    active = Optional.of(activeList);
                }
            }

            if (active.isPresent()) {
                map.put(line, active.get());
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        for (Map.Entry<Integer, List<CodeBlock>> entry : map.entrySet()) {
            JSONArray array = new JSONArray();

            for (CodeBlock block : entry.getValue()) {
                array.put(block.getMatchId());
            }

            object.put(""+entry.getKey(), array);
        }
        return object;
    }

    public String getHighlightedLines() {
        return map.keySet().stream().map(Object::toString).collect(Collectors.joining(","));
    }
}