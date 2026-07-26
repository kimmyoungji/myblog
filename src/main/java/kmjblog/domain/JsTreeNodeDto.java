package kmjblog.domain;

import java.util.ArrayList;
import java.util.List;

public class JsTreeNodeDto {

    private String id;
    private String parent;
    private String text;
    private String type;
    private Object data;

    public JsTreeNodeDto(
    		String id,
    		String parent,
            String text,
            String type,
            Object data
    ) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        this.type = type;
        this.data = data;
    }

    public String getId() {
        return id;
    }
    
    public String getParent() {
        return parent;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public Object getData() {
        return data;
    }
}