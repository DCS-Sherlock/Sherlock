package uk.ac.warwick.dcs.sherlock.module.web.models.results;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMapper {
    //map<FILE_ID, LINE_MAPPER>
    private Map<Long, LineMapper> map;

    public FileMapper(List<Match> matches) {
        this.map = new HashMap<>();

        //Loop through all the matches
        for (Match match : matches) {
            long file1 = match.getFile1Id();
            long file2 = match.getFile2Id();

            //Ensure that the map contains an entry for both files
            if (!map.containsKey(file1)) {
                map.put(file1, new LineMapper(file1));
            }

            if (!map.containsKey(file2)) {
                map.put(file2, new LineMapper(file2));
            }

            //Each map should now be filled with line numbers where matched code
            //blocks start. If multiple matches start on the same line, the list
            //should have multiple elements in it, all others should only contain
            //one element
            map.get(file1).AddStartLines(match);
            map.get(file2).AddStartLines(match);
        }

        //Loop through all the files and fill the gaps
        for (Map.Entry<Long, LineMapper> entry : map.entrySet()) {
            entry.getValue().FillInBlocks();
        }
    }

    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        for (Map.Entry<Long, LineMapper> entry : map.entrySet()) {
            object.put(""+entry.getKey(), entry.getValue().toJSON());
        }

        return object;
    }

    public String getHighlightedLines(long fileId) {
        if (!map.containsKey(fileId)) {
            return "";
        }

        return map.get(fileId).getHighlightedLines();
    }
}
