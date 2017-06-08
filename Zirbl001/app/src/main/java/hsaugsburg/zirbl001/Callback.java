package hsaugsburg.zirbl001;


import java.util.List;

public interface Callback {
    void processData(List<JSONModel> result);
}
