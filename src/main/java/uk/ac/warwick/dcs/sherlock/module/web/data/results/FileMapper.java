package uk.ac.warwick.dcs.sherlock.module.web.data.results;

import org.json.JSONObject;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.internal.CodeBlock;
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
     *
     * @throws MapperException if add match was called after fill
     */
    public FileMapper(List<FileMatch> matches) throws MapperException {
        this.map = new HashMap<>();

        //Loop through all the matches
        for (FileMatch match : matches) {
            for (Map.Entry<ISourceFile, List<CodeBlock>> entry : match.getMap().entrySet()) {
                ISourceFile entryFile = entry.getKey();
                List<CodeBlock> entryList = entry.getValue();

                long entryFileId = entryFile.getPersistentId();

                //Ensure that the map contains an entry for both files
                if (!map.containsKey(entryFileId)) {
                    map.put(entryFileId, new LineMapper(entryFileId));
                }

                map.get(entryFileId).AddMatch(match);
            }
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
     * @param fileId the id of the file to get the highlighted lines for
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
