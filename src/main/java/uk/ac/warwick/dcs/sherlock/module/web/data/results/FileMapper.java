package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.FileMatch;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.MapperException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps a set of files to the "line mapper" object which links each
 * plagiarised line to a match
 */
public class FileMapper {
    /**
     * A map linking each file id (Long) to the line mapper
     */
    private Map<Long, LineMapper> map;

    /**
     * Initialise the file mapper
     *
     * @param matches the list of matches to initialise with
     */
    public FileMapper(List<FileMatch> matches) throws MapperException {
        this.map = new HashMap<>();

        //Loop through all the matches
        for (FileMatch match : matches) {
            long file1 = match.getFile1Id();
            long file2 = match.getFile2Id();

            //Ensure that the map contains an entry for both files
            if (!map.containsKey(file1)) {
                map.put(file1, new LineMapper(file1));
            }

            if (!map.containsKey(file2)) {
                map.put(file2, new LineMapper(file2));
            }

            map.get(file1).AddMatch(match);
            map.get(file2).AddMatch(match);
        }

        //Loop through all the files and fill the gaps
        for (Map.Entry<Long, LineMapper> entry : map.entrySet()) {
            entry.getValue().Fill();
        }
    }

    /**
     * Convert this object to a JSON object, used by the JavaScript in the UI
     *
     * @return the JSON equivalent of this object
     */
    public JSONObject toJSON() {
        JSONObject object = new JSONObject();

        for (Map.Entry<Long, LineMapper> entry : map.entrySet()) {
            object.put(""+entry.getKey(), entry.getValue().toJSON());
        }

        return object;
    }

    /**
     * Get the list of plagiarised line numbers converted to a comma separated list for
     * a specific file in the map
     *
     * e.g. if lines 2-10 are mapped to a match, this would return "2,3,4,5,6,7,8,9,10"
     *
     * @param fileId
     *
     * @return the comma separated list, or an empty list if the file isn't found
     */
    public String getHighlightedLines(long fileId) {
        if (!map.containsKey(fileId)) {
            return "";
        }

        return map.get(fileId).getHighlightedLines();
    }
}