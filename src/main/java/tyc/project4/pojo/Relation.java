package tyc.project4.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Relation {
    private String rowkey;
    private Map attends;
    private Map concerneds;
}
